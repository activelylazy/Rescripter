
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

function onlySourceMatches(match) {
    if (match.getElement().getClass().isAssignableFrom(org.eclipse.jdt.internal.core.ResolvedSourceMethod)) {
        return true;
    } else if (match.getElement().getClass().isAssignableFrom(org.eclipse.jdt.internal.core.ResolvedSourceField)) {
        return true;
    } else if (match.getElement().getClass().isAssignableFrom(org.eclipse.jdt.internal.core.Initializer)) {
        return true;
    } else if (match.getElement().getClass().isAssignableFrom(org.eclipse.jdt.internal.core.ResolvedBinaryMethod)) {
        return false;
    } else {
        throw "Unexpected class "+match.getElement().getClass();
    }
}

var Rename = {
	method: function(method, newName) {
		return new org.eclipse.text.edits.ReplaceEdit(method.getNameRange().getOffset(), method.getNameRange().getLength(), newName)
	}
};

function SourceChange(cu) {
	this.cu = cu;
	this.textEdit = new org.eclipse.text.edits.MultiTextEdit();
	this.imports = {};
	
	this.apply = function() {
		this.cu.becomeWorkingCopy(null);
		this.cu.applyTextEdit(this.textEdit, null);
		this.cu.commitWorkingCopy(true, null);
	};
	
	this.addEdit = function(textedit) {
        if (textedit != undefined) {	   
		    this.textEdit.addChild(textedit);
		}
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
	
	this.addImport = function(cu, import) {
	   if (this.imports[import] == undefined) {
	       this.imports[import] = import;
	       this.addEdit(addImport(cu, import));
	   }
	   return this;
	}
	
	return this;
}

function addImport(cu, text) {
    var matching = filter(cu.getImports(), function(import) {
        return import.getElementName() == text;
    });
    if (matching.length == 0) {
        var lastImport = first(cu.getImports())
        var offset = lastImport.getSourceRange().getOffset() + lastImport.getSourceRange().getLength();
        return new org.eclipse.text.edits.InsertEdit(offset, "\nimport "+text+";");
    }
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
