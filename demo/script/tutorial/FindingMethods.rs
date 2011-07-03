
/*
 * First find a method on person by name alone
 */
var type = Find.typeByName("Person");
Alert.info(Find.methodByName(type, "getName").getElementName());

/*
 * Can also find a list of methods by name
 */
Alert.info(Find.methodsByName(type, "setAge").length);

/*
 * We can also find methods by signature from the type
 */
Alert.info(type.getMethod("setName",["QString;"]).getElementName());
