
//Alert.info("Hello world");

//Alert.info(Find.typeByName("Person").getFullyQualifiedName());

var type = Find.typeByName("Person");
Alert.info(type.getMethod("getName",[]).getElementName());
Alert.info(findMethodByName(type, "getName").getElementName());