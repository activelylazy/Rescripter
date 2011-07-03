var edit = new MultiSourceChange();

var person = Find.typeByName("Person");
var method = Find.methodByName(person, "getName");
foreach(Find.referencesTo(method), function(reference) {
    var identifier = first(filter(ScanTokens.in(person.getCompilationUnit(), reference.getOffset(), reference.getLength()),
                                  isAnIdentifier));
    edit.changeFile(reference.getElement().getCompilationUnit())
        .replace(identifier.getOffset(), identifier.getLength(), "getUserName");                                  
});

edit.changeFile(person.getCompilationUnit())
    .addEdit(Rename.method(method, "getUserName"));

edit.apply();

Alert.info("Done");

function isAnIdentifier(token) { 
    return token.tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier
}