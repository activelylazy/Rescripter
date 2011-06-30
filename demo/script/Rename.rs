
var person = Find.typeByName("Person");
var getName = findMethodByName(person, "getName");
var references = Find.referencesTo(getName);

for(var i=0; i<references.length; i++) {
    var methodIdentifier =  ASTTokenFinder.findTokenOfType(references[i].getElement().getCompilationUnit(),
                                                           org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier,
                                                           references[i].getOffset(),
                                                           references[i].getLength());
    ChangeText.inCompilationUnit(references[i].getElement().getCompilationUnit(),
                                 methodIdentifier.getOffset(), methodIdentifier.getLength(),
                                 "getUserName");    
}

ChangeText.inCompilationUnit(person.getCompilationUnit(), getName.getNameRange().getOffset(), getName.getNameRange().getLength(), "getUserName");
