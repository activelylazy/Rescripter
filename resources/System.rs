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
		var methodRange = ASTTokenFinder.findIdentifier(references[i].getElement().getCompilationUnit(),
														references[i].getOffset(),
														references[i].getLength());
		ChangeText.inCompilationUnit(references[i].getElement().getCompilationUnit(),
									 startOfNew, methodRange.getOffset()+methodRange.getLength()-startOfNew,
									 constructor.getDeclaringType().getElementName()+"."+newMethodName);
	}
}
