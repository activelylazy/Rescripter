
function findMethodsByName(type, methodName) {
	var methods = type.getMethods();
	var matching = [];
	for (var i=0; i<methods.length; i++) {
		if (methods[i].getElementName() == methodName) {
			matching.push(methods[i]);
		}
	}
	return matching;
}

function moveMethodBetweenInjectables() {
	var targetService = Find.typeByName("DataService");
	var targetServiceConstructor = findMethodByName(targetService, "DataService");
	
	var sourceService = Find.typeByName("GodClass");
	var sourceServiceConstructor = findMethodByName(sourceService, "GodClass");
	
	if (!isInjectable(sourceServiceConstructor)) {
		Alert.error("Source class is not injectable");
		return;
	} 
	if (!isInjectable(targetServiceConstructor)) {
		Alert.error("Target class is not injectable");
		return;
	}

	var methodToMove = findMethodByName(sourceService, "someBusinessLogic");
	if (methodToMove == undefined) {
		Alert.error("No such method to move");
		return;
	}
	
	var references = Find.referencesTo(methodToMove);
	for(var i=0; i<references.length; i++) {
		var refType = references[i].getElement().getDeclaringType();
		var newFieldName = initLowerCase(targetService.getElementName());
		if (!typeDefinesFieldOfType(refType, targetService)) {
			changeReferenceFieldTo(references[i], newFieldName);
			addField(refType, targetService);
			addImport(refType.getCompilationUnit(), targetService);
			addInjectableToConstructors(findMethodsByName(refType, refType.getElementName()), targetService, newFieldName);
			refType.getCompilationUnit().commitWorkingCopy(true, null);
		}
	}
	
	methodToMove.move(targetService, null, null, false, null);
	sourceService.getCompilationUnit().commitWorkingCopy(true, null);
	targetService.getCompilationUnit().commitWorkingCopy(true, null);
}

function addInjectableToConstructors(constructors, paramType, paramName) {
	for(var i=0; i<constructors.length; i++) {
		// Only amend @Inject constructors, we cannot know what to do with others
		if (isInjectable(constructors[i])) {
			assignParameterToField(constructors[i], paramType, paramName, paramName);
			addParameterToMethod(constructors[i], paramType, paramName);
		}
	}
}

function assignParameterToField(method, paramType, paramName, fieldName) {
	ChangeText.inCompilationUnit(method.getDeclaringType().getCompilationUnit(),
								 method.getSourceRange().getOffset() + method.getSourceRange().getLength() - 1,
								 0, "this."+fieldName+" = "+paramName+";\n");
}

function addParameterToMethod(method, paramType, paramName) {
    var endParamList =  ASTTokenFinder.findTokenOfType(method.getDeclaringType().getCompilationUnit(),
                                                       org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameRPAREN,
                                                       method.getSourceRange().getOffset(),
                                                       method.getSourceRange().getLength());
	ChangeText.inCompilationUnit(method.getDeclaringType().getCompilationUnit(),
								 endParamList.getOffset(), 0, ", "+paramType.getElementName()+" "+paramName);		                                                       
}

var lastIdentifierOffset;
var lastIdentifierLength;
var found = false;
function changeReferenceFieldTo(reference, newField) {
	ASTTokenFinder.scanTokens(reference.getElement().getCompilationUnit(), 
								reference.getElement().getSourceRange().getOffset(), 
								reference.getOffset() - reference.getElement().getSourceRange().getOffset(),
		function(tokenType, offset, length) {
			if (!found && tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier) {
				if (offset == reference.getOffset()) {
					found = true;
					return;
				}
				lastIdentifierOffset = offset;
				lastIdentifierLength = length;
			}
		}
	);

	if (lastIdentifierLength == 0) {
		Alert.error("Couldn't figure out the field calling the method to move");
		return;
	}
	ChangeText.inCompilationUnit(reference.getElement().getCompilationUnit(),
									lastIdentifierOffset, lastIdentifierLength, newField);
}

function addImport(compilationUnit, importType) {
	compilationUnit.createImport(importType.getFullyQualifiedName(), 
		null, org.eclipse.jdt.core.Flags.AccDefault, null);
}

function addField(container, fieldType) {
	var decl = "private "+fieldType.getElementName()+" "+initLowerCase(fieldType.getElementName())+";";
	container.createField(decl,null, false, null);
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

moveMethodBetweenInjectables();