
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
 * Now find a method on person by signature
 */
Alert.info(type.getMethod("setName",["QString;"]).getElementName());
