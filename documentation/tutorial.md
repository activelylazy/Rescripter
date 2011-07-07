# Rescripter Tutorial

## Getting Started

Once you've installed the plugin from the [update site](https://raw.github.com/activelylazy/Rescripter/master/update-site/).
Create a new file with the extension .rs. The Rescripter editor lets you enter and run JavaScript, this JavaScript has access to the Eclipse plugin environment, as well as some helper objects to make searching, parsing and modifying Java code easier. 

Simply enter the script you require; then to run it press Ctrl-R S.

All the below examples are in the demo project. If you pull the Rescripter project, the demo directory has an Eclipse project setup ready to run the demos.

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
    private String name;
    private int age;

    public Person(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public void setAge(String age) { this.age = Integer.parseInt(age); }
}
```

```java
Alert.info(Find.typeByName("Person").getFullyQualifiedName());
```
	
This will popup a message box with the fully qualified name of the class named Person, i.e. "com.example.Person". Find.typeByName returns an [org.eclipse.jdt.core.IType](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html).

## Finding Methods
There are three ways to find a method on a type.

### Find method by name
The simplest is to use Find.methodByName(type, methodName). For example:

```java
var type = Find.typeByName("Person");
Alert.info(Find.methodByName(type, "getName").getElementName());
```

This displays the method name of Person.getName - i.e. "getName". If there are multiple methods with the same name (but different signatures), an error is thrown.

### Find methods by name
The next way is to use the finder object to find multiple methods by name, for example:

```java
Alert.info(Find.methodsByName(type, "setAge").length);
```

This tells us that there are 2 methods named setAge in the Person class.

### Find method by signature
Finally, we can find methods by providing their signature. 

```java
Alert.info(type.getMethod("setName",["QString;"]).getElementName());
```

This relies on [IType.getMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IType.html#getMethod(java.lang.String, java.lang.String[]).

All of these approaches return [org.eclipse.jdt.core.IMethod](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IMethod.html)s.

## Finding References
Given a method, one thing we might want to do is find all the places where the method is used. There are two separate ways to do this. Both of which return an array of [org.eclipse.jdt.core.search.SearchMatch](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/search/SearchMatch.html).

### Find references to IMethod
The first way is to search for references to the IMethod

```java
var type = Find.typeByName("Person");
var method = Find.methodByName(type,"getName");
var references = Find.referencesTo(method);
Alert.info("There are "+references.length+" references to getName()");
```

This tells us there is 1 reference to the getName method.

### Find references by signature
The second way is to search for a reference to method by it's signature.

```java
references = Search.forReferencesToMethod("com.example.Person(String)");
Alert.info("There are "+references.length+" references to the constructor"); 
```

## Scanning the AST
Although IType, IMethod and SearchMatch provide the locations of some key parts of the source tree, sometimes we need a more fine-grained view of the source code. 

For example, when we have a search match identifying a method call, we get the whole method invocation - both the method name and the parameter list. I.e. the part of the line:
    System.out.println("The name is "+person.```getName()```);

If we, say, wanted to rename the method, we need to identify just the method name and ignore the parentheses and parameters. we can do this by using the token scanner.

```java
var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");
var references = Find.referencesTo(method);

var tokens = ScanTokens.in(type.getCompilationUnit(), 
                           references[0].getOffset(),
                           references[0].getLength());

var identifiers = filter(tokens, 
                      function(token) { 
                          return token.tokenType == org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier
                      });

Alert.info("Found identifier: "+identifiers[0]+". Text is "+identifiers[0].getSource());
```

There are two key steps to this.

### Scan tokens
First we get a list of tokens. We get this by scanning the source of the type's file, but only the range identified by the reference. I.e. this is the section of the source file that covers the method call ```getName()```.

### Filter to identifiers
We then filter this to just identifier tokens (see [org.eclipse.jdt.core.compiler.ITerminalSymbols](http://help.eclipse.org/helios/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/compiler/ITerminalSymbols.html) for a complete list). The first identifier must be the method name. The source range of this gives us its exact position:
    System.out.println("The name is "+person.```getName```());
 
## Making Changes
Having identified types, methods and tokens within source files, we might want to make changes to the source code. 

### Insert Source Change
The simplest change we can make is to insert text into the source file.

```java
var type = Find.typeByName("Person");
var method = Find.methodByName(type, "getName");

var edit = new SourceChange(type.getCompilationUnit());
edit.insert(method.getSourceRange().getOffset(),
            "/* This is a comment */\n\t"); 
edit.apply();
```

This simply identifies the getName method and adds a comment immediately before. So we now have:

```java
    /* This is a comment */
    public String getName() { return name; }
```

The SourceChange allows us to collect a sequence of edits and apply in one go. The offsets & lengths identify positions within the _unchanged_ file. Once a change has been applied to a file, any source ranges identified before the change are invalid and must be re-calculated.

### Add Import
We can also add an import to a file:

```java
edit = new SourceChange(type.getCompilationUnit());
edit.addImport("java.util.List");
edit.apply();
```

After running this, an import statement appears at the top of the file. The import is only added if there isn't already a matching import.

### Add Field
We can also add fields to types.

```java
var type = Find.typeByName("Person");
var stringType = Find.typeByName("java.lang.String");

edit = new SourceChange(type.getCompilationUnit());
edit.addEdit(ChangeType.addField(type, stringType, "jobTitle"));
edit.apply();
```

### Add Method
We can also add new methods to a type.

```java
edit = new SourceChange(type.getCompilationUnit());
edit.addEdit(ChangeType.addMethod(type, "\n\tpublic String getJobTitle() {\n"+
                                          "\t\treturn this.jobTitle;\n"+
                                          "\t}"));
edit.apply();
```

