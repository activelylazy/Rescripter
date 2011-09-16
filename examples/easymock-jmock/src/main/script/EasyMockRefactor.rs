
var EasyMockRefactor = function() { };

EasyMockRefactor.prototype.refactorAll = function() {
    createMockMethod = Find.methodByName(Find.typeByName("org.easymock.EasyMock"), "createMock");
    //var references = Search.forReferencesToMethod(createMockMethod);
    this.refactorClass();
};
        
EasyMockRefactor.prototype.refactorClass = function() {
};

EasyMockRefactor.prototype.refactorMethod = function() {
};
        

