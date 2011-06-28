package com.rescripter.syntax;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;

public class ChangeText {

    public void in(ICompilationUnit compilationUnit, int position, int length, String newText) throws JavaModelException {
        try {
            WorkingCopyOwner owner = DefaultWorkingCopyOwner.PRIMARY;
            ICompilationUnit workingCopy = compilationUnit.getWorkingCopy(owner, null);
            IBuffer buffer = workingCopy.getBuffer();
            buffer.replace(position, length, newText);
            workingCopy.reconcile(ICompilationUnit.NO_AST, true, owner, null);
            compilationUnit.commitWorkingCopy(true, null);
        } finally {
            compilationUnit.discardWorkingCopy();
        }
    }
    

}
