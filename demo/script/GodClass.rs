
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
			//addField(refType, targetService);
			Alert.info("No field of type "+targetService.getElementName()+" in "+refType.getElementName());
		}
	}
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