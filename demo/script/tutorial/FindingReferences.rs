
/*
 * Given a method we can find references to it
 */
var type = Find.typeByName("Person");
var method = Find.methodByName(type,"getName");
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName()");

/*
 * We can also search by signature
 */
Alert.info("There are "+Search.forReferencesToMethod("com.example.Person(String)").length+" references to the constructor"); 