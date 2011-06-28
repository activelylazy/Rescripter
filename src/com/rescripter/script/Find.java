package com.rescripter.script;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class Find {

    public IType typeByName(String name) throws CoreException {
        SearchPattern pattern = SearchPattern.createPattern(name, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
        if (pattern == null) {
            throw new NullPointerException("No pattern!?");
        }
        
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        
        SearchRequestor requestor = new SearchRequestor() {
            @Override public void acceptSearchMatch(SearchMatch match) throws CoreException {
                matches.add(match);
            }
        };
        
        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.search(pattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope, requestor, null);

        if (matches.size() != 1) {
            throw new IllegalArgumentException("Failed to find unique type");
        }

        return (IType) matches.get(0).getElement();
    }
    
    public SearchMatch[] referencesTo(IJavaElement element) throws CoreException {
        final List<SearchMatch> references = new ArrayList<SearchMatch>();
        
        SearchPattern pattern = SearchPattern.createPattern(element, IJavaSearchConstants.REFERENCES);
        if (pattern == null) {
            // E.g. element not found / no longer exists
            throw new NullPointerException("No pattern!?");
        }
        
        SearchRequestor requestor = new SearchRequestor() {
            @Override public void acceptSearchMatch(SearchMatch match) throws CoreException {
                references.add(match);
            }
        };
        
        new SearchEngine().search(pattern,
                                  new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
                                  SearchEngine.createWorkspaceScope(),
                                  requestor,
                                  null);
        
        return references.toArray(new SearchMatch[]{});
    }
}
