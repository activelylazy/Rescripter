
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

function findMethodByName(type, methodName) {
	var methods = type.getMethods();
	for (var i=0; i<methods.length; i++) {
		if (methods[i].getElementName() == methodName) {
			return methods[i];
		}
	}
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
