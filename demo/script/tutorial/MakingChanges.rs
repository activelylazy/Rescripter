var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");

var edit = new SourceChange(type.getCompilationUnit());
edit.insert(method.getSourceRange().getOffset(),
            "/* This is a comment */\n\t"); 
edit.apply();