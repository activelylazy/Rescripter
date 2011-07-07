var type = Find.typeByName("Person");
var stringType = Find.typeByName("java.lang.String");

edit = new SourceChange(type.getCompilationUnit());
edit.addEdit(ChangeType.addField(type, stringType, "jobTitle"));
edit.apply();