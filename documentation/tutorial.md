# Rescripter Tutorial

## Getting Started

Once you've installed the plugin from the [update site](https://raw.github.com/activelylazy/Rescripter/master/update-site/).
Create a new file with the extension .rs. The Rescripter editor lets you enter and run JavaScript, with some helpers that expose Eclipse's internal model of your Java code. 

Simply enter the script you require; then to run it press Ctrl-R S.

## Hello World
To display messages in your script - e.g. success when you're done (or for primitive debugging).

```java
Alert.info("Hello world");
```

## Finding Types
To find Java types by name

```java
package com.example.rename;

public class Person {
    public Person() { }
    
    public String getName() { return "Fred"; }
    
    public static void main(String[] args) {
        Person person = new Person();
        System.out.println("The name is "+person.getName());
    }
}
```

```java
Alert.info(Find.typeByName("Person").getFullyQualifiedName());
```
	
This will popup a message box with the fully qualified name of the class named Person, i.e. "com.example.Person". Find.typeByName returns an [org.eclipse.jdt.core.IType](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html).

## Finding Methods
There are two ways to find a method on a type. The first is to use [IType.getMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html#getMethod(java.lang.String, java.lang.String[]). Alternatively there's a helper method for the simple case where the method isn't overloaded.

```java
var type = Find.typeByName("Person");
Alert.info(type.getMethod("getName",[]).getElementName());
Alert.info(findMethodByName(type, "getName").getElementName());
```

Both of these approaches return an [org.eclipse.jdt.core.IMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IMethod.html).

## Finding References
Given a method, the next most interesting thing to do is find all the places its referenced from.

```java
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName(); the first is in "+references[0].getElement().getElementName());
```

This tells us that there is just 1 reference - in the main method. Find.referencesTo returns an array of [org.eclipse.jdt.core.search.SearchMatch](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/search/SearchMatch.html).

## Scanning the AST
The various elements provide a fine-grained view of the source code - but it doesn't always provide enough detail. However, it is possible to use Eclipse's AST scanner to further parse the source - the ASTTokenFinder provides an interface to this.

```java
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
var references = Find.referencesTo(method);
Alert.info(ASTTokenFinder.findTokenOfType(type.getCompilationUnit(), 
					  org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLPAREN, 
                                	  references[0].getOffset(),
					  references[0].getLength()));
```

This searches for references to the Person.getName(). We then build an AST and scan the section covered by the reference (i.e. the part of the line:
System.out.println("The name is "+person.```getName()```);

Within that section we then find the first '(' symbol. We now have the location within the source file of the "getName" reference, leaving the field and parameters out.

## Making Changes
Through Eclipse's plugin environment we have access to various tools that can make changes to code. However, to simplify
common cases where we want free-form changes there is a helper method.

```java
var type = Find.typeByName("Person");
var method = type.getMethod("getName",[]);
ChangeText.inCompilationUnit(type.getCompilationUnit(),
                             method.getSourceRange().getOffset(), 
                             0,
                             "/* This is a comment */\n\t"); 
```

This amends the source in Person.java and adds a comment before the getName() method, so we now have:

```java
    public Person() { }
    
    /* This is a comment */
    public String getName() {
        return "Fred";
    }
```
