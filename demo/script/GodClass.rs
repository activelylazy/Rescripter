function findConstructors(type) {
	return Find.methodsByName(type, type.getElementName());
}

function findInjectableConstructors(type) {
	return filter(findConstructors(type),
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
	   var newFieldName = initLowerCase(toType.getElementName());
	   if(!typeDefinesFieldOfType(refType, toType)) {
	       edit.changeFile(refType.getCompilationUnit())
	           .addEdit(addField(refType, toType, newFieldName))
	           .addEdit(addImport(refType.getCompilationUnit(), toType));
	           
	       foreach(findInjectableConstructors(refType), function(constructor){
	           edit.changeFile(refType.getCompilationUnit())
	               .addEdit(addParameterToMethod(constructor, toType, newFieldName))
	               .addEdit(assignParameterToField(constructor, newFieldName, newFieldName));
	       });
	   }
	   
	   edit.changeFile(refType.getCompilationUnit())
	       .addEdit(changeReferenceFieldTo(reference, newFieldName));
	});
	
	
	edit.apply();

    fromMethod.move(toType, null, null, false, null);
}

function last(list) {
    return list[list.length-1];
}

function addField(toType, fieldType, fieldName) {
    var lastField = last(toType.getFields());
    var offset = lastField.getSourceRange().getOffset() + lastField.getSourceRange().getLength();
    var decl = "\n\tprivate "+fieldType.getElementName()+" "+fieldName+";";
    return new org.eclipse.text.edits.InsertEdit(offset, decl);
}

function addImport(compilationUnit, importType) {
    var lastImport = last(compilationUnit.getImports());
    var offset = lastImport.getSourceRange().getOffset() + lastImport.getSourceRange().getLength();
    var imp = "\nimport "+importType.getFullyQualifiedName()+";";
    return new org.eclipse.text.edits.InsertEdit(offset, imp);    
}

function addParameterToMethod(method, paramType, paramName) {
    var params = method.getParameters();
    var lastParam = last(params);
    var offset = lastParam.getSourceRange().getOffset() + lastParam.getSourceRange().getLength();
    
    return new org.eclipse.text.edits.InsertEdit(offset, ", "+paramType.getElementName()+" "+paramName);
}

function assignParameterToField(method, paramName, fieldName) {
    var cu = method.getDeclaringType().getCompilationUnit();
    var range = method.getSourceRange();
    var offset = range.getOffset() + range.getLength() - 1;
    var stmt = "this."+fieldName+" = "+paramName+";\n";
    return new org.eclipse.text.edits.InsertEdit(offset, stmt);
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



/*
 * Not used, so far
 */
function reformatFile(cu) {
    var formatter = org.eclipse.jdt.core.ToolFactory.createCodeFormatter(null);
    return formatter.format(org.eclipse.jdt.core.formatter.CodeFormatter.K_COMPILATION_UNIT, 
                            cu.getSource(),
                            cu.getSourceRange().getOffset(),
                            cu.getSourceRange().getLength(), 0, null);
}







function initLowerCase(value) {
	return value.substring(0,1).toLowerCase() + value.substring(1);
}

function typeDefinesFieldOfType(container, fieldType) {
	var fields = container.getFields();
	for(var i=0; i<fields.length; i++) {
		if (org.eclipse.jdt.core.Signature.toString(fields[i].getTypeSignature()) == fieldType.getElementName()) {
			return true;
		}
	}
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