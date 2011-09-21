
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

describe("easy mock class refactor", function() {
    it("should process each method", function() {
        var someType = { getMethods : function() { } },
            refactorClass = new EasyMockClassRefactor(someType),
            methodOne = "method one",
            methodTwo = "method two",
            methods = [ methodOne, methodTwo ],
            methodRefactor = jasmine.createSpyObj("methodRefactor", ["refactor"]);
        
        spyOn(window, "EasyMockMethodRefactor").andReturn(methodRefactor);
        spyOn(someType, "getMethods").andReturn(methods);
        
        refactorClass.refactor();
        
        expect(window.EasyMockMethodRefactor.callCount).toEqual(2);
        expect(window.EasyMockMethodRefactor).toHaveBeenCalledWith(methodOne);
        expect(window.EasyMockMethodRefactor).toHaveBeenCalledWith(methodTwo);
    });
});

describe("easy mock method refactor", function() {
    it("should get a list of mocks", function() {
        var method = "some method",
            easyMockType = "easy mock type",
            createMockMethod = "create mock method",
            reference1 = "reference 1",
            reference2 = "reference 2",
            references = [ reference1, reference2 ],
            refactorMethod = new EasyMockMethodRefactor(method),
            mock = jasmine.createSpyObj("mock", [ "todo" ]);

        spyOn(Find, "typeByName").andReturn(easyMockType);
        spyOn(Find, "methodByName").andReturn(createMockMethod);
        spyOn(Find, "referencesWithinType").andReturn(references);
        spyOn(window, "Mock").andReturn(mock);
            
        refactorMethod.refactor();
        
        expect(Find.typeByName).toHaveBeenCalledWith("org.easymock.EasyMock");
        expect(Find.methodByName).toHaveBeenCalledWith(easyMockType, "createMock");
        expect(Find.referencesWithinType).toHaveBeenCalledWith(createMockMethod, method);
        expect(window.Mock.callCount).toEqual(2);
        expect(window.Mock).toHaveBeenCalledWith(reference1);
        expect(window.Mock).toHaveBeenCalledWith(reference2);
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