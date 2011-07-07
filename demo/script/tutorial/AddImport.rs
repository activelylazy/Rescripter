var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");

edit = new SourceChange(type.getCompilationUnit());
edit.addImport("java.util.List");
edit.apply();