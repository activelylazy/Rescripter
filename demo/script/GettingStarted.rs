

/*
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName()");
*/

var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info(ASTTokenFinder.findTokenOfType(type.getCompilationUnit(), org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN, 
                                references[0].getOffset(), references[0].getLength()));

//var type = Find.typeByName("Person");
//var method = type.getMethod("getName",[]);
//ChangeText.inCompilationUnit(type.getCompilationUnit(), method.getSourceRange().getOffset(), 0,
//                            "/* This is a comment */\n\t"); 
