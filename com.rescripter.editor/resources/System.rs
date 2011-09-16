
var Find = function() {
}

Find.typeByName = function(name) {
	return SearchHelper.findTypeByName(name);
}
	
Find.methodByName = function(type, methodName) {
	var methods = type.getMethods();
	for (var i=0; i<methods.length; i++) {
		if (methods[i].getElementName() == methodName) {
			return methods[i];
		}
	}
}

Find.methodsByName = function(type, methodName) {
	var methods = type.getMethods();
	var matching = [];
	for (var i=0; i<methods.length; i++) {
		if (methods[i].getElementName() == methodName) {
			matching.push(methods[i]);
		}
	}
	return matching;
}

Find.referencesTo = function(element) {
   return SearchHelper.findReferencesTo(element);
}

Find.referencesWithinType = function(element, withinType) {
	return SearchHelper.findReferencesTo(element, withinType);
}

Find.constructors = function(type) {
	return Find.methodsByName(type, type.getElementName());
}

Find.fieldOfType = function (container, fieldType) {
    var fields = container.getFields();
    for(var i=0; i<fields.length; i++) {
        if (org.eclipse.jdt.core.Signature.toString(fields[i].getTypeSignature()) == fieldType.getElementName()) {
            return fields[i];
        }
    }
}

var Search = function() { };

Search.forReferencesToMethod = function(signature) {
    return SearchHelper.findMethodReferences(signature)
}

Search.onlySourceMatches = function (match) {
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
	this.imports = {};
	
	this.apply = function() {
		this.cu.becomeWorkingCopy(null);
		this.cu.applyTextEdit(this.textEdit, null);
		this.cu.commitWorkingCopy(true, null);
	};
	
	this.addEdit = function(textedit) {
        if (textedit != undefined) {
            var newStart = textedit.getOffset();
            var newEnd = textedit.getOffset() + textedit.getLength();
            var isDuplicate = false;
            foreach(this.textEdit.getChildren(),function(child) {
                var existingStart = child.getOffset();
                var existingEnd = child.getOffset() + child.getLength();
                
                if (existingEnd == newEnd && existingStart == newStart && newEnd > newStart && textedit.getClass().getName() == child.getClass().getName()) {
                    Debug.message("[WARN] Duplicate text edit "+textedit);
                    isDuplicate = true;
                    return;
                }
                if (existingEnd > newStart && existingStart < newEnd) {
                    Debug.message("[ERROR] Overlapping text edit");
                    Debug.message("    New edit "+textedit);
                    Debug.message("    Overlaps with "+child);
                    throw "Attempt to add overlapping text edit";
                }                
            });
            if (!isDuplicate) {	   
		        this.textEdit.addChild(textedit);
		    }
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
	
	this.delete = function(offset, length) {
	   this.addEdit(new org.eclipse.text.edits.DeleteEdit(offset, length));
	   return this;
    }
	
	this.addImport = function(import) {
	   if (this.imports[import] == undefined) {
	       this.imports[import] = import;
	       this.addEdit(Refactor.createImportEdit(this.cu, import));
	   }
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

var Refactor = {
    createImportEdit: function(cu, text) {
        var matching = filter(cu.getImports(), function(import) {
            return import.getElementName() == text;
        });
        if (matching.length == 0) {
            var offset;
            var lastImport = first(cu.getImports())
            if (lastImport == undefined) {
                var packageDecl = cu.getPackageDeclarations()[0];
                offset = packageDecl.getSourceRange().getOffset() + packageDecl.getSourceRange().getLength();
            } else {
                offset = lastImport.getSourceRange().getOffset();
            }
            return new org.eclipse.text.edits.InsertEdit(offset, "import "+text+";\n");
        }
    },

	createReplaceMethodCallEdit: function(cu, offset, length, newCall) {
	    var startOfNew = offset;
	    var endOfCons = ASTTokenFinder.findTokenOfType(cu,
	                                                   org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN,
	                                                   offset,
	                                                   length)
	                        .getOffset();
	    return new org.eclipse.text.edits.ReplaceEdit(startOfNew, endOfCons-startOfNew, newCall);
	}
}

var ChangeType = {
    addField: function(toType, fieldTypeName, fieldName, fieldInit) {
	    var lastField = first(toType.getFields());
	    var offset;
	    if (lastField == null) {
            var brace = first(filter(ScanTokens.in(toType.getCompilationUnit(), toType.getSourceRange().getOffset(), toType.getSourceRange().getLength()),
                                     function(token) {
                                         return token.tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLBRACE;
                                     }));
            offset = parseInt(brace.getOffset()) + parseInt(brace.getLength());
	    } else {
	       offset = lastField.getSourceRange().getOffset() + lastField.getSourceRange().getLength();
	    }
	    var decl = "\n\tprivate "+fieldTypeName+" "+fieldName+(fieldInit == undefined ? "" : " = "+fieldInit) + ";";
	    return new org.eclipse.text.edits.InsertEdit(offset, decl);
	},

    addMethod: function (toType, decl) {
        var lastMethod = last(toType.getMethods());
        var offset = lastMethod.getSourceRange().getOffset() + lastMethod.getSourceRange().getLength();
        return new org.eclipse.text.edits.InsertEdit(offset, decl);
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
