function findMethodByName(type, methodName) {
	var methods = type.getMethods();
	for (var i=0; i<methods.length; i++) {
		if (methods[i].getElementName() == methodName) {
			return methods[i];
		}
	}
}

function replaceConstructorCall(constructor, newMethodName) {
	var references = Find.referencesTo(constructor);
	
	for(var i=0; i<references.length; i++) {
		var startOfNew = references[i].offset;
        var endOfCons = ASTTokenFinder.findTokenOfType(references[i].getElement().getCompilationUnit(),
                                                        org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN,
                                                        references[i].getOffset(),
                                                        references[i].getLength())
                            .getOffset();
        ChangeText.inCompilationUnit(references[i].getElement().getCompilationUnit(),
                                     startOfNew, endOfCons-startOfNew,
                                     constructor.getDeclaringType().getElementName()+"."+newMethodName);
	}
}
