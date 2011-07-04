
var Find = {
	typeByName: function(name) {
		return SearchHelper.findTypeByName(name);
	},
	
	methodByName: function(type, methodName) {
		var methods = type.getMethods();
		for (var i=0; i<methods.length; i++) {
			if (methods[i].getElementName() == methodName) {
				return methods[i];
			}
		}
	},
	
	methodsByName: function(type, methodName) {
		var methods = type.getMethods();
		var matching = [];
		for (var i=0; i<methods.length; i++) {
			if (methods[i].getElementName() == methodName) {
				matching.push(methods[i]);
			}
		}
		return matching;
	},
	
	referencesTo: function(element) {
		return SearchHelper.findReferencesTo(element);
	},

	constructors: function(type) {
		return Find.methodsByName(type, type.getElementName());
	}
	
};


function Token(cu, tokenType, offset, length) {
	this.cu = cu;
	this.tokenType = tokenType;
	this.offset = offset;
	this.length = length;
	
	this.toString = function() {
		return "Token(tokenType: "+tokenType+", offset: "+offset+", length: "+length+")";
	};
	
	this.getSource = function() {
		return cu.getSource().substring(offset, parseInt(offset)+parseInt(length));
	};
	
	this.getOffset = function() {
		return offset;
	};
	
	this.getLength = function() {
		return length;
	};
	
	return this;
}

var ScanTokens = {
	in: function(cu, offset, length) {
		var tokens = [];
		ASTTokenFinder.scanTokens(cu, offset, length, function(tokenType, offset, length) {
			tokens.push(new Token(cu, tokenType, offset, length));
		});
		return tokens;	
	}
};

var Rename = {
	method: function(method, newName) {
		return new org.eclipse.text.edits.ReplaceEdit(method.getNameRange().getOffset(), method.getNameRange().getLength(), newName)
	}
};

function SourceChange(cu) {
	this.cu = cu;
	this.textEdit = new org.eclipse.text.edits.MultiTextEdit();
	
	this.apply = function() {
		this.cu.becomeWorkingCopy(null);
		this.cu.applyTextEdit(this.textEdit, null);
		this.cu.commitWorkingCopy(true, null);
	};
	
	this.addEdit = function(textedit) {
		this.textEdit.addChild(textedit);
		return this;
	}
	
	this.insert = function(offset, text) {
        this.addEdit(new org.eclipse.text.edits.InsertEdit(offset, text));
        return this;
	}
	
	this.replace = function(offset, length, text) {
		this.addEdit(new org.eclipse.text.edits.ReplaceEdit(offset, length, text));
		return this;
	}
	
	return this;
}

function MultiSourceChange() {
    this.dict = {};
    
    this.changeFile = function(file) {
        if (this.dict[file] == undefined) {
            this.dict[file] = new SourceChange(file);
        }
        return this.dict[file];
    };
    
    this.apply = function() {
        for(key in this.dict) {
            this.dict[key].apply();
        }
    }
    
    return this;
}

function first(list) {  
    return list[0];
}

function last(list) {
    return list[list.length-1];
}

function foreach(from, fun) {
    for(var i=0; i<from.length; i++) {
        fun(from[i]);
    }
}

function transform(from, fun) {
	var results = [];
	for(var i=0; i<from.length; i++) {
		results.push(fun(from[i]));
	}
	return results;
}

function filter(from, test) {
	var results = [];
	for(var i=0; i<from.length; i++) {
		if (test(from[i])) {
			results.push(from[i]);
		}		
	}
	return results;
}

function replaceConstructorCall(constructor, newMethodName, useStaticImport) {
	var newMethod = constructor.getDeclaringType().getMethod(newMethodName, constructor.getParameterTypes());
	if (newMethod.getSignature() == undefined) {
		Alert.error("Failed to find "+constructor.getDeclaringType().getFullyQualifiedName()+"."+newMethodName);
	}
	var references = SearchHelper.findReferencesTo(constructor);
	
	for(var i=0; i<references.length; i++) {
		var startOfNew = references[i].offset;
        var endOfCons = ASTTokenFinder.findTokenOfType(references[i].getElement().getCompilationUnit(),
                                                       org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN,
                                                       references[i].getOffset(),
                                                       references[i].getLength())
                            .getOffset();
                            
        if (useStaticImport) {
	        ChangeText.inCompilationUnit(references[i].getElement().getCompilationUnit(),
	                                     startOfNew, endOfCons-startOfNew,
	                                     newMethodName);
        	references[i].getElement().getCompilationUnit().createImport(
        			constructor.getDeclaringType().getFullyQualifiedName()+"."+newMethodName,
        			null, org.eclipse.jdt.core.Flags.AccStatic, null);
        	references[i].getElement().getCompilationUnit().commitWorkingCopy(true, null);
        } else {
	        ChangeText.inCompilationUnit(references[i].getElement().getCompilationUnit(),
	                                     startOfNew, endOfCons-startOfNew,
	                                     constructor.getDeclaringType().getElementName()+"."+newMethodName);
        }
	}
}

var ChangeType = {
    addField: function(toType, fieldType, fieldName) {
	    var lastField = last(toType.getFields());
	    var offset = lastField.getSourceRange().getOffset() + lastField.getSourceRange().getLength();
	    var decl = "\n\tprivate "+fieldType.getElementName()+" "+fieldName+";";
	    return new org.eclipse.text.edits.InsertEdit(offset, decl);
	},

	addImport: function(compilationUnit, importType) {
	    var lastImport = last(compilationUnit.getImports());
	    var offset = lastImport.getSourceRange().getOffset() + lastImport.getSourceRange().getLength();
	    var imp = "\nimport "+importType.getFullyQualifiedName()+";";
	    return new org.eclipse.text.edits.InsertEdit(offset, imp);    
	},

	addParameterToMethod: function(method, paramType, paramName) {
	    var params = method.getParameters();
	    var lastParam = last(params);
	    var offset = lastParam.getSourceRange().getOffset() + lastParam.getSourceRange().getLength();
	    
	    return new org.eclipse.text.edits.InsertEdit(offset, ", "+paramType.getElementName()+" "+paramName);
	},

	assignParameterToField: function(method, paramName, fieldName) {
	    var cu = method.getDeclaringType().getCompilationUnit();
	    var range = method.getSourceRange();
	    var offset = range.getOffset() + range.getLength() - 1;
	    var stmt = "this."+fieldName+" = "+paramName+";\n";
	    return new org.eclipse.text.edits.InsertEdit(offset, stmt);
	}

};

