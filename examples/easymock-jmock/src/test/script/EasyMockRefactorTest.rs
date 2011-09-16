
Load.file('rescripter/jasmine-rhino.js');
Load.file('rescripter/jasmine-rescripter.js');
Load.file('jasmine/jasmine.js');

Load.file('../../main/script/EasyMockRefactor.rs');

describe("easy mock refactor", function() {

    it("finds all easy mock classes and processes each", function() {
        var refactor = new EasyMockRefactor(),
            easyMockType = "easy mock type",
            createMockMethod = "create mock method";

        spyOn(Find, "typeByName").andReturn(easyMockType);
        spyOn(Find, "methodByName").andReturn(createMockMethod);
        spyOn(refactor, "refactorClass");
        spyOn(Search, "forReferencesToMethod");
        
        refactor.refactorAll();
        
        expect(Find.typeByName).toHaveBeenCalledWith("org.easymock.EasyMock");
        expect(Find.methodByName).toHaveBeenCalledWith(easyMockType, "createMock");
        expect(refactor.refactorClass).toHaveBeenCalled();
        expect(Search.forReferencesToMethod).toHaveBeenCalledWith(createMockMethod);
    });
/*
    it("finds all easy mock classes and processes each method", function() {
    
        refactor = new EasyMockRefactor();
        
        spyOn(refactor, "refactorMethod");
        expect(refactor.refactorMethod).toHaveBeenCalled();    
    
    });
*/
});

runJasmine();