package com.rescripter.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class RSType {

    private final IType type;

    public RSType(IType type) {
        this.type = type;
    }
    
    public String getFullyQualifiedName() {
        return type.getFullyQualifiedName();
    }
    
    public RSMethod[] getMethods() throws JavaModelException {
        List<RSMethod> methods = new ArrayList<RSMethod>();
        for (IMethod method : type.getMethods()) {
            methods.add(new RSMethod(method));
        }
        
        return methods.toArray(new RSMethod[]{});
    }
    
    public RSMethod findMethodByName(String name) throws JavaModelException {
        IMethod theMethod = null;
        for (IMethod method : type.getMethods()) {
            if (method.getElementName().equals(name)) {
                if (theMethod != null) {
                    throw new IllegalArgumentException("There are multiple methods in type "+type.getElementName()+" with name "+name);
                }
                theMethod = method;
            }
        }
        return new RSMethod(theMethod);
    }
}
