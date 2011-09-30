
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
    var ast = AST.parseCompilationUnit(reference.getElement().getCompilationUnit());
    var node = AST.findCoveredNode(ast, reference.offset, reference.length);

    assert(node.getClass().isAssignableFrom(org.eclipse.jdt.core.dom.MethodInvocation), "createMock reference is not a MethodInvocation");

    var parent = node.getParent();
    if (parent.getClass().isAssignableFrom(org.eclipse.jdt.core.dom.VariableDeclarationFragment)) {
        this.name = "" + parent.getName();
        this.type = parent.getParent().getType(); 
                        
        return;
    }
    
    throw "Cannot parse createMock reference in "+parent;
}
