var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");

/*
 * First let's add a comment to the getName method
 */
var edit = new SourceChange(type.getCompilationUnit());
edit.insert(method.getSourceRange().getOffset(),
            "/* This is a comment */\n\t"); 
edit.apply();

/*
 * We can also add an import to the file
 */
edit = new SourceChange(type.getCompilationUnit());
edit.addImport("java.util.List");
edit.apply();
