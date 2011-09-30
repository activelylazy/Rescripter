
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

describe("create mock", function() {
    it("should parse the ast and find a method invocation", function() {
        var cu = "compilation unit",
            reference = {
                getElement : function() { return {
                    getCompilationUnit : function() { return cu; }
                } }
            },
            ast = "the ast",
            node = a_node_of_type(org.eclipse.jdt.core.dom.MethodInvocation,
                       a_variable_declaration_fragment("no name",
                           a_variable_declaration_statement("no type")));
        
        spyOn(AST, "parseCompilationUnit").andReturn(ast);
        spyOn(AST, "findCoveredNode").andReturn(node);
        
        var mock = new Mock(reference);
    });
    
    it("should throw an error if not a method invocation", function() {
        var cu = "compilation unit",
            reference = {
                getElement : function() { return {
                    getCompilationUnit : function() { return cu; }
                } }
            },
            ast = "the ast",
            node = a_node_of_type("not a method invocation"),
            mock;
                
        spyOn(AST, "parseCompilationUnit").andReturn(ast);
        spyOn(AST, "findCoveredNode").andReturn(node);
        
        try {
            new Mock(reference);
            throw "Expected call to new Mock to throw error";
        } catch (e) {
            expect(e).toEqual("Assertion failed: createMock reference is not a MethodInvocation");
        }
    });
    
    it("should throw an error if parent type cannot be parsed", function() {
        var cu = "compilation unit",
            reference = {
                getElement : function() { return {
                    getCompilationUnit : function() { return cu; }
                } }
            },
            ast = "the ast",
            node = a_node_of_type(org.eclipse.jdt.core.dom.MethodInvocation,
                       a_node_of_type("not a variable declaration"));
        
        spyOn(AST, "parseCompilationUnit").andReturn(ast);
        spyOn(AST, "findCoveredNode").andReturn(node);
        
        try {
            new Mock(reference);
            throw "Expected call to new Mock to throw error";
        } catch (e) {
            expect(e).toEqual("Cannot parse createMock reference in a node of type not a variable declaration");
        }
    });
    
    it("should extract the name & type from a variable declaration", function() {
        var cu = "compilation unit",
            reference = {
                getElement : function() { return {
                    getCompilationUnit : function() { return cu; }
                } }
            },
            ast = "the ast",
            node = a_node_of_type(org.eclipse.jdt.core.dom.MethodInvocation,
                       a_variable_declaration_fragment("theField",
                           a_variable_declaration_statement("theMockType")));
            
        spyOn(AST, "parseCompilationUnit").andReturn(ast);
        spyOn(AST, "findCoveredNode").andReturn(node);

        var mock = new Mock(reference);
        
        expect(mock.name).toEqual("theField");
        expect(mock.type.getName()).toEqual("theMockType");
    }); 
});

function a_variable_declaration_fragment(name, parent) {
    var node = a_node_of_type(org.eclipse.jdt.core.dom.VariableDeclarationFragment, parent);
    node.getName = function() {
        return name;
    }
    return node;
}

function a_variable_declaration_statement(type, parent) {
    var node = a_node_of_type(org.eclipse.jdt.core.dom.VariableDeclarationStatement, parent);
    node.getType = function() {
        return {
            getName : function() {
                return type;
            }
        };
    };
    return node;
}

function a_node_of_type(type, parent) {
    return {
	    getClass : function() { return {
	        isAssignableFrom : function(theType) {
	            return type === theType;
	        }
	    } },
	    getParent : function() { return parent; },
	    toString : function() { return "a node of type " + type; }
	};
}

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