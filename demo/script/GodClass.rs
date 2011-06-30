
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