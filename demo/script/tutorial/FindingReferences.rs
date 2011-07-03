
/*
 * Given a method we can find references to it
 */
var type = Find.typeByName("Person");
var method = Find.methodByName(type,"getName");
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName()");
