function findInjectableConstructors(type) {
	return filter(Find.constructors(type),
				  function(constructor) {
				      return isInjectable(constructor);
				  });
}

function moveMethodBetweenInjectables(fromMethod, toType) {
    var fromType = fromMethod.getDeclaringType();
	var fromConstructors = findInjectableConstructors(fromType);
	var toConstructors = findInjectableConstructors(toType);
	
	if (fromConstructors.length < 1 || toConstructors.length < 1) {
		Alert.error("Can only move methods between classes that have an @Inject constructor");
		return;
	}
	var edit = new MultiSourceChange();
	
	foreach(Find.referencesTo(fromMethod), function(reference) {
	   var refType = reference.getElement().getDeclaringType();
	   var field = Find.fieldOfType(refType, toType);
	   if(field == undefined) {
		   var newFieldName = initLowerCase(toType.getElementName());
	       edit.changeFile(refType.getCompilationUnit())
	           .addEdit(ChangeType.addField(refType, toType, newFieldName))
	           .addEdit(ChangeType.addImport(refType.getCompilationUnit(), toType));
	           
	       foreach(findInjectableConstructors(refType), function(constructor){
	           edit.changeFile(refType.getCompilationUnit())
	               .addEdit(ChangeType.addParameterToMethod(constructor, toType, newFieldName))
	               .addEdit(ChangeType.assignParameterToField(constructor, newFieldName, newFieldName));
	       });
	   } else {
	       newFieldName = field.getElementName();
	   }
	   
	   edit.changeFile(refType.getCompilationUnit())
	       .addEdit(changeReferenceFieldTo(reference, newFieldName));
	});
	
	
	edit.apply();
	
	var methodName = fromMethod.getElementName();
	var params = fromMethod.getParameterTypes();

    fromMethod.getDeclaringType().getCompilationUnit().becomeWorkingCopy(null);
    toType.getCompilationUnit().becomeWorkingCopy(null);
    fromMethod.move(toType, null, null, false, null);
    fromMethod.getDeclaringType().getCompilationUnit().commitWorkingCopy(true, null);
    toType.getCompilationUnit().commitWorkingCopy(true, null);
    
    edit = new MultiSourceChange();
    
    // Reformat the method we moved
    var newMethod = Find.methodByName(toType, methodName);
    edit.changeFile(toType.getCompilationUnit())
        .addEdit(reformat(toType.getCompilationUnit(), newMethod.getSourceRange().getOffset(), newMethod.getSourceRange().getLength()));
        
    // Reformat each of the @Inject constructors in each class that references this
    foreach(Find.referencesTo(newMethod), function(reference) {
        var type = reference.getElement().getDeclaringType();
        var cu = type.getCompilationUnit();
        foreach(findInjectableConstructors(type), function(constructor) {
	        edit.changeFile(cu)
	            .addEdit(reformat(cu,
	                              constructor.getSourceRange().getOffset(),
	                              constructor.getSourceRange().getLength()));
	    }); 
	});       
    
    edit.apply();
        
/*    
    edit = new MultiSourceChange();

    // Now re-format each method that calls us
    foreach(Find.referencesTo(toType.getMethod(methodName, params)), function(reference) {
        var cu = reference.getElement().getDeclaringType().getCompilationUnit();
        //Alert.info("Reference is to "+reference.getElement().getSourceRange());
        Alert.info("Reformatting "+cu.getSource().substring(reference.getElement().getSourceRange().getOffset(), reference.getElement().getSourceRange().getOffset() + reference.getElement().getSourceRange().getLength()));
        var reformatEdit = reformat(cu, reference.getElement().getSourceRange().getOffset(), reference.getElement().getSourceRange().getLength());
        if (reformatEdit != undefined) {
	        edit.changeFile(cu)
	            .addEdit(reformatEdit);
	    } else {
	       Alert.info("Failed to reformat "+reference);
	    }
    });
    edit.apply();
*/    
}

function changeReferenceFieldTo(reference, newFieldName) {
    var lastIdentifier = last(filter(ScanTokens.in(reference.getElement().getCompilationUnit(),
                                                   reference.getElement().getSourceRange().getOffset(),
                                                   reference.getOffset() - 1 - reference.getElement().getSourceRange().getOffset()),
                              function(match) {
                                  return match.tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier;
                              }));

    if (lastIdentifier == undefined) {
        Alert.error("Couldn't figure out the field calling the method to move");
        return;
    }
    
    return new org.eclipse.text.edits.ReplaceEdit(lastIdentifier.getOffset(), lastIdentifier.getLength(), newFieldName);
}

function reformat(cu, offset, length) {
    var formatter = org.eclipse.jdt.core.ToolFactory.createCodeFormatter(null);
    return formatter.format(org.eclipse.jdt.core.formatter.CodeFormatter.K_UNKNOWN, 
                            cu.getSource(),
                            offset,
                            length, 0, null);
}

function initLowerCase(value) {
	return value.substring(0,1).toLowerCase() + value.substring(1);
}

function isInjectable(method) {
	var annotations = method.getAnnotations();
	for(var i=0; i<annotations.length; i++) {
		if (annotations[i].getElementName() == "Inject") {
			return true;
		}
	}
	return false;
}

moveMethodBetweenInjectables(Find.methodByName(Find.typeByName("GodClass"), "someBusinessLogic"),
                             Find.typeByName("DataService"));