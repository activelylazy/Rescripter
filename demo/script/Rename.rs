var person = Find.typeByName("Person");
var method = Find.methodByName(person, "getName");

var edit = new SourceChange(person.getCompilationUnit())
edit.addEdit(Rename.method(method, "getUserName"));
edit.apply();

Alert.info("Done");