# Rescripter Tutorial

## Getting Started

Once you've installed the plugin from the [update site](https://raw.github.com/activelylazy/Rescripter/master/update-site/).
Create a new file with the extension .rs. To run the script press Ctrl-R S.

## Hello World
To display messages in your script - e.g. success when you're done (or for primitive debugging).
```
Alert.info("Hello world");
```

## Finding Types
To find Java types by name

```
package com.example.rename;

public class Person {

    public Person() {
        
    }
    
    public String getName() {
        return "Fred";
    }
    
    public static void main(String[] args) {
        Person person = new Person();
        
        System.out.println("The name is "+person.getName());
    }
}
```

```
Alert.info(Find.typeByName("Person").getFullyQualifiedName());
```
	
This will popup a message box saying "com.example.Person". Find.typeByName returns an [org.eclipse.jdt.core.IType](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html).

## Finding Methods
There are two ways to find a method on a type. The first is to use [IType.getMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html#getMethod(java.lang.String, java.lang.String[]). Alternatively there's a helper method for the simple case where the method isn't overloaded.
```
var type = Find.typeByName("Person");
Alert.info(type.getMethod("getName",[]).getElementName());
Alert.info(findMethodByName(type, "getName").getElementName());
```

Both of these approaches return an [org.eclipse.jdt.core.IMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IMethod.html).

## Finding References
Given a method, the next most interesting thing to do is find all the places its referenced from.

```
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName()");
```

This tells us that there is just 1 reference. Find.referencesTo returns an array of [org.eclipse.jdt.core.search.SearchMatch](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/search/SearchMatch.html).

## Scanning the AST
The various elements provide a fine-grained view of the source code - but it doesn't always provide enough detail. However, it is possible to use Eclipse's AST scanner to further parse the source.

```
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info(ASTTokenFinder.findTokenOfType(type.getCompilationUnit(), org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN, 
                                references[0].getOffset(), references[0].getLength()));
```
This searches for references to the getName() method; then for the first reference it builds an AST and scans the section of the method call. From this we find the first '(' symbol and show this in the alert box. The findTokenOfType method returns a [org.eclipse.jdt.core.SourceRange](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/SourceRange.html) identifying the position of the first matching token.

## Making Changes
Through Eclipse's plugin environment we have access to various tools that can make changes to code. However, to simplify
common cases where we want free-form changes there is a helper method.

```
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
ChangeText.inCompilationUnit(type.getCompilationUnit(), method.getSourceRange().getOffset(), 0,
                            "/* This is a comment */\n\t"); 
```

This amends the source in Person.java and adds a comment before the getName() method, so we now have:

```
    public Person() {
        
    }
    
    /* This is a comment */
    public String getName() {
        return "Fred";
    }
```

