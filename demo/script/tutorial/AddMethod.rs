
var type = Find.typeByName("Person");

edit = new SourceChange(type.getCompilationUnit());
edit.addEdit(ChangeType.addMethod(type, "\n\tpublic String getJobTitle() {\n"+
                                          "\t\treturn this.jobTitle;\n"+
                                          "\t}"));
edit.apply();

    

