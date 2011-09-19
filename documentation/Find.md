
# Find

The Find class contains various helper methods for finding types and methods.

# typeByName(name)
*Find a Java type by class name*

* name is either the fully qualified name (com.example.PersonFactory) or just the class name (PersonBuilder)

Returns an IType, or *undefined* if there is no such type.

## methodByName(type, methodName)
/Finds a specific method in a type by name/
+ type is an IType
+ methodName is the name of the method - e.g. getUsername

Returns an IMethod, or *undefined* if there is no such type.

## methodsByName(type, methodName)
/Finds a list of methods with a specific name/
+ type is an IType
+ methodName is the name of the method - e.g. getUsername

Returns an array of IMethod

## referencesTo(element)
/Finds references to an element - e.g. uses of a type or method/
+ element is an IJavaElement - e.g. types, methods, import declarations, variable declarations etc...

Returns an array of SearchMatch.

## referencesWithType(element, withinType)
/Finds references to an element, but bounds the search to a specific type - e.g. find all calls of some method within a specific class/
+ element is an IJavaElemet - e.g. types, methods, import declarations, variable declarations etc...
+ withinType is an IType

## constructors(type)
/Finds a type's constructors/
+ type is an IType

Returns an array of IMethod

## fieldOfType(container, fieldType)
/Finds a field with a specific type/
+ container is an IType
+ fieldType is the type of field to find

Returns an IField, or *undefined* if none match. Returns the first if there are multiple


