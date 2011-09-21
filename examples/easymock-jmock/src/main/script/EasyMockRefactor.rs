
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
};
