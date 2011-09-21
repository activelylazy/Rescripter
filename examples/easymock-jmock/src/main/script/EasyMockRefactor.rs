
var EasyMockRefactor = function() { };

EasyMockRefactor.prototype.refactorAll = function() {
    createMockMethod = Find.methodByName(Find.typeByName("org.easymock.EasyMock"), "createMock");
    var references = filter(Find.referencesTo(createMockMethod), Search.onlySourceMatches);
    var refactor = this;
    foreach(references, function(reference) {
        refactor.refactorClass(reference.getElement().getParent());
    }); 
};

EasyMockRefactor.prototype.refactorClass = function(type) {
};

EasyMockRefactor.prototype.refactorMethod = function() {
};
        

