
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
