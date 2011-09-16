package com.rescripter.syntax;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class ASTTokenFinder {

    public ISourceRange findIdentifier(ICompilationUnit theCU, int offset, int length) throws JavaModelException {
        return findTokenOfType(theCU, ITerminalSymbols.TokenNameIdentifier, offset, length);
    }
    
    public ISourceRange findTokenOfType(ICompilationUnit theCU, int tokenType, int offset, int length) throws JavaModelException {
        IScanner scanner = ToolFactory.createScanner(false, false, false, false);
        scanner.setSource(theCU.getSource().toCharArray());
        scanner.resetTo(offset, offset + length);
        int token;
        try {
            while ((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
                if (token == tokenType) {
                    return new SourceRange(scanner.getCurrentTokenStartPosition(), 
                                           scanner.getCurrentTokenEndPosition() + 1 - scanner.getCurrentTokenStartPosition());
                }
            }
        } catch (InvalidInputException e) {
            throw new RuntimeException("Error finding token: "+e.getMessage(), e);
        }
        
        return null;
    }
    
    public static interface FoundToken {
        void found(int tokenType, int offset, int length);
    }
    
    public void scanTokens(ICompilationUnit theCU, int offset, int length, FoundToken callback) throws JavaModelException {
        IScanner scanner = ToolFactory.createScanner(false, false, false, false);
        scanner.setSource(theCU.getSource().toCharArray());
        scanner.resetTo(offset, offset + length);
        int token;
        try {
            while ((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
                callback.found(token, scanner.getCurrentTokenStartPosition(), scanner.getCurrentTokenEndPosition() + 1 - scanner.getCurrentTokenStartPosition());
            }
        } catch (InvalidInputException e) {
            throw new RuntimeException("Error finding token: "+e.getMessage(), e);
        }
    }
    
}

/**
 * Copied SourceRange to be compatible with Eclipse 3.5 and 3.6.
 */
class SourceRange implements ISourceRange {

	protected final int offset, length;

	public SourceRange(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ISourceRange)) {
			return false;
		}
		ISourceRange sourceRange = (ISourceRange) obj;
		return sourceRange.getOffset() == this.offset && sourceRange.getLength() == this.length;
	}

	public int getLength() {
		return this.length;
	}

	public int getOffset() {
		return this.offset;
	}

	@Override
	public int hashCode() {
		return this.length ^ this.offset;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[offset="); //$NON-NLS-1$
		buffer.append(this.offset);
		buffer.append(", length="); //$NON-NLS-1$
		buffer.append(this.length);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}
}
