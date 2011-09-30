
Load.file('rescripter/jasmine-rhino.js');
Load.file('rescripter/jasmine-rescripter.js');
Load.file('jasmine/jasmine.js');

Load.file('../../main/script/EasyMockRefactor.rs');

describe("easy mock refactor", function() {

    it("should create a mock from a variable declaration", function() {
        var test = Find.typeByName("TillServiceEasyMockTest");
        var method = Find.methodByName(test, "unauthenticated_user_cannot_start_shopping");
        expect(method).toBeDefined();
        
        var createMockMethod = Find.methodByName(Find.typeByName("EasyMock"), "createMock");
        expect(createMockMethod).toBeDefined();
        
        var references = Find.referencesWithinType(createMockMethod, method);
        expect(references).toBeDefined();
        expect(references.length).toBe(1);
        
        var mock = new Mock(references[0]);
        
        expect(mock.name).toEqual("authenticationService");
        expect("" + mock.type.getName()).toBe("AuthenticationService");
    });

});

runJasmine();