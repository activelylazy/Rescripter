package com.rescripter.syntax;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class ASTTokenFinder {

    public SourceRange findIdentifier(ICompilationUnit theCU, int offset, int length) throws JavaModelException {
        IScanner scanner = ToolFactory.createScanner(false, false, false, false);
        scanner.setSource(theCU.getSource().toCharArray());
        scanner.resetTo(offset, offset + length);
        int token;
        try {
            while ((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
                switch(token) {
                case ITerminalSymbols.TokenNameIdentifier:
                    return new SourceRange(scanner.getCurrentTokenStartPosition(), 
                                           scanner.getCurrentTokenEndPosition() + 1 - scanner.getCurrentTokenStartPosition());
                }
            }
        } catch (InvalidInputException e) {
            throw new RuntimeException("Error finding token: "+e.getMessage(), e);
        }
        
        return null;
    }
    
}
