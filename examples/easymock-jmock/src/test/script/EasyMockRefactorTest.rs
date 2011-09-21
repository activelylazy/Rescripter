
Load.file('rescripter/jasmine-rhino.js');
Load.file('rescripter/jasmine-rescripter.js');
Load.file('jasmine/jasmine.js');

Load.file('../../main/script/EasyMockRefactor.rs');

describe("easy mock refactor", function() {

    it("should be created", function() {
        var refactor = new EasyMockRefactor();
        
        expect(refactor).toBeDefined();
    })
    
    it("processes each class in turn", function() {
        var refactor = new EasyMockRefactor(),
            easyMockType = "easy mock type",
            createMockMethod = "create mock method",
            firstType = "first type",
            secondType = "second type",
            firstReference = an_element_in(firstType),
            secondReference = an_element_in(secondType),
            createMockReferences = [ firstReference, secondReference ],
            classRefactor = jasmine.createSpyObj("classRefactor", ["refactor"]);
            

        spyOn(Find, "typeByName").andReturn(easyMockType);
        spyOn(Find, "methodByName").andReturn(createMockMethod);
        spyOn(Find, "referencesTo").andReturn(createMockReferences);
        spyOn(Search, "onlySourceMatches").andReturn(true);
        spyOn(window, "EasyMockClassRefactor").andReturn(classRefactor);
        
        refactor.refactorAll();
        
        expect(Find.typeByName).toHaveBeenCalledWith("org.easymock.EasyMock");
        expect(Find.methodByName).toHaveBeenCalledWith(easyMockType, "createMock");
        expect(Find.referencesTo).toHaveBeenCalledWith(createMockMethod);
        expect(Search.onlySourceMatches.callCount).toEqual(2);
        expect(Search.onlySourceMatches).toHaveBeenCalledWith(firstReference);
        expect(Search.onlySourceMatches).toHaveBeenCalledWith(secondReference);
        expect(window.EasyMockClassRefactor).toHaveBeenCalledWith(firstType);
        expect(window.EasyMockClassRefactor).toHaveBeenCalledWith(secondType);
        expect(classRefactor.refactor.callCount).toEqual(2);
    });

});

function an_element_in(type) {
    return {
        getElement : function() {
            return {
                getParent : function() {
                    return type;
                }
            };
        }
    };
}


runJasmine();