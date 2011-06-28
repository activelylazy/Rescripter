package com.rescripter.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.MethodReferenceMatch;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class RSMethod {

    private final IMethod method;

    public RSMethod(IMethod method) {
        this.method = method;
    }
    
    public String getName() {
        return method.getElementName();
    }
    
    public void setName(String newName) throws JavaModelException {
        new ChangeText().inCompilationUnit(method.getCompilationUnit(), method.getNameRange().getOffset(), method.getNameRange().getLength(), newName);
    }
    
    public RSMethodReference[] findReferences() throws CoreException {
        final List<RSMethodReference> references = new ArrayList<RSMethodReference>();
        
        SearchPattern pattern = SearchPattern.createPattern(method, IJavaSearchConstants.REFERENCES);
        if (pattern == null) {
            // E.g. element not found / no longer exists
            throw new NullPointerException("No pattern!?");
        }
        
        SearchRequestor requestor = new SearchRequestor() {
            @Override public void acceptSearchMatch(SearchMatch match) throws CoreException {
                references.add(new RSMethodReference((MethodReferenceMatch) match));
            }
        };
        
        new SearchEngine().search(pattern,
                                  new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
                                  SearchEngine.createWorkspaceScope(),
                                  requestor,
                                  null);
        
        return references.toArray(new RSMethodReference[]{});
    }
}
