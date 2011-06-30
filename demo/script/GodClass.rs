
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
		if (!typeDefinesFieldOfType(refType, targetService)) {
			changeReferenceFieldTo(references[i], initLowerCase(targetService.getElementName()));
			addField(refType, targetService);
			addImport(refType.getCompilationUnit(), targetService);
			refType.getCompilationUnit().commitWorkingCopy(true, null);
		}
	}
}

function debugElement(element) {
	Alert.info("Have: "+element.getCompilationUnit().getSource().substring(element.getOffset(), reference.getOffset()+reference.getLength()));
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
/*
    var fieldRange =  ASTTokenFinder.findTokenOfType(reference.getElement().getCompilationUnit(),
                                                           org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameDOT,
                                                           reference.getOffset(),
                                                           reference.getLength());
	ChangeText.inCompilationUnit(reference.getElement().getCompilationUnit(),
								 fieldRange.getOffset(), fieldRange.getLenght(), newField);
*/
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