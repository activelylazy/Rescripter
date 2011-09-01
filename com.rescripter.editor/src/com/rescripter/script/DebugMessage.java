package com.rescripter.script;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

public class DebugMessage {

    public void message(String message) {
        System.out.println(message);
    }
    
    public void sourceRange(ICompilationUnit cu, ISourceRange sourceRange) throws JavaModelException {
        System.out.println(cu.getSource().substring(sourceRange.getOffset(), sourceRange.getOffset() + sourceRange.getLength()));
    }
}
