package com.rescripter.syntax;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.search.MethodReferenceMatch;

public class RSMethodReference {

    private final MethodReferenceMatch match;

    public RSMethodReference(MethodReferenceMatch match) {
        this.match = match;
    }

    public void setMethodName(String newName) throws JavaModelException {
        ICompilationUnit theCU = (ICompilationUnit) JavaCore.create((IFile) match.getResource());

        SourceRange range = new ASTTokenFinder().findIdentifier(theCU, match.getOffset(), match.getLength());

        new ChangeText().inCompilationUnit(theCU, range.getOffset(), range.getLength(), newName);
    }
}
