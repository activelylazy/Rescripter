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
	var references = Find.referencesTo(constructor);
	
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

function addImport(compilationUnit, importText) {
	var imports = compilationUnit.getImports();
	var lastImport = 0;
	for(var i=0; i<imports.length; i++) {
		var pos = imports[i].getSourceRange().getOffset() + imports[i].getSourceRange().getLength();
		if (pos > lastImport) {
			lastImport = pos;
		}
	}
	if (lastImport == 0) {
		lastImport = compilationUnit.getPackageDeclarations()[0].getSourceRange().getOffset() +
					 compilationUnit.getPackageDeclarations()[0].getSourceRange().getLength();
	}
	ChangeText.inCompilationUnit(compilationUnit, lastImport, 0,
								 "\r\n"+importText);
}
