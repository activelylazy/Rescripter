package com.rescripter.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.rescripter.resources.WorkspaceScriptLoader;

public class WorkspaceScriptLoaderMatcher extends TypeSafeDiagnosingMatcher<WorkspaceScriptLoader> {
	
	private final String expectedFile;

	private WorkspaceScriptLoaderMatcher(String expectedFile) {
		this.expectedFile = expectedFile;
	}
	
	public static WorkspaceScriptLoaderMatcher a_loader_relative_to(String expectedPath) {
		return new WorkspaceScriptLoaderMatcher(expectedPath);
	}

	public void describeTo(Description description) {
		description.appendText("a workspace script loader relative to " + expectedFile);
	}

	@Override
	protected boolean matchesSafely(WorkspaceScriptLoader loader, Description description) {
		return loader.getCurrentLocation().getFullPath().toPortableString().equals(expectedFile);
	}
	
}