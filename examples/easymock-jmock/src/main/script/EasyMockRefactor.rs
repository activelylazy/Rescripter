
var EasyMockRefactor = function() { };

EasyMockRefactor.prototype.refactorAll = function() {
    createMockMethod = Find.methodByName(Find.typeByName("org.easymock.EasyMock"), "createMock");
    var references = filter(Find.referencesTo(createMockMethod), Search.onlySourceMatches);
    var refactor = this;
    foreach(references, function(reference) {
        new EasyMockClassRefactor(reference.getElement().getParent()).refactor();
    }); 
};

EasyMockRefactor.prototype.refactorMethod = function() {
};
        
var EasyMockClassRefactor = function(type) { this.type = type; }

EasyMockClassRefactor.prototype.refactor = function() {
    foreach(this.type.getMethods(), function(each) {
        new EasyMockMethodRefactor(each).refactor();
    });
};


var EasyMockMethodRefactor = function(method) { this.method = method; }

EasyMockMethodRefactor.prototype.refactor = function() {
    createMockMethod = Find.methodByName(Find.typeByName("org.easymock.EasyMock"), "createMock");
    var references = Find.referencesWithinType(createMockMethod, this.method);
    var mocks = transform(references, function(each) {
        new Mock(each);
    });
};


var Mock = function(reference) {
//    var s = reference.getElement().getCompilationUnit().getSource().substring(reference.offset, reference.offset+reference.length);
//    Debug.message("It's: "+s);

    var parser = org.eclipse.jdt.core.dom.ASTParser.newParser(org.eclipse.jdt.core.dom.AST.JLS3);
    parser.setKind(org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT);
    parser.setSource(reference.getElement().getCompilationUnit());
    parser.setResolveBindings(true);
    
    var ast = parser.createAST(null);
    
    var node = org.eclipse.jdt.core.dom.NodeFinder.perform(ast, reference.offset, reference.length);
    
    Debug.message("Found "+node+" - which is a "+node.getClass().getName());
}
