/*
 * First find Person.getName() and find references to it
 */
var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");
var references = Find.referencesTo(method);

/*
 * Then parse the syntax of the first reference and find the identifier tokens
 */
var tokens = ScanTokens.in(type.getCompilationUnit(), 
                           references[0].getOffset(),
                           references[0].getLength());
                           
var identifiers = filter(tokens, 
                      function(token) { 
                          return token.tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier
                      });

Alert.info("Found identifier: "+identifiers[0]+". Text is "+identifiers[0].getSource());
