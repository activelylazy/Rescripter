var Find = function() {
}

Find.typeByName = function(name) {
    return SearchHelper.findTypeByName(name);
}
    
Find.methodByName = function(type, methodName) {
    var methods = type.getMethods();
    for (var i=0; i<methods.length; i++) {
        if (methods[i].getElementName() == methodName) {
            return methods[i];
        }
    }
    throw "Could not find method "+methodName+" on "+type.getName();
}

Find.methodsByName = function(type, methodName) {
    var methods = type.getMethods();
    var matching = [];
    for (var i=0; i<methods.length; i++) {
        if (methods[i].getElementName() == methodName) {
            matching.push(methods[i]);
        }
    }
    return matching;
}

Find.referencesTo = function(element) {
   return SearchHelper.findReferencesTo(element);
}

Find.referencesWithinType = function(element, withinType) {
    return SearchHelper.findReferencesTo(element, withinType);
}

Find.constructors = function(type) {
    return Find.methodsByName(type, type.getElementName());
}

Find.fieldOfType = function (container, fieldType) {
    var fields = container.getFields();
    for(var i=0; i<fields.length; i++) {
        if (org.eclipse.jdt.core.Signature.toString(fields[i].getTypeSignature()) == fieldType.getElementName()) {
            return fields[i];
        }
    }
}

