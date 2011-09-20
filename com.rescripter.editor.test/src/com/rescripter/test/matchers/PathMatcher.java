package com.rescripter.test.matchers;

import org.eclipse.core.runtime.IPath;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class PathMatcher extends TypeSafeDiagnosingMatcher<IPath> {
	
	private final String expectedPath;

	private PathMatcher(String expectedPath) {
		this.expectedPath = expectedPath;
	}
	
	public static PathMatcher a_path_matching(String expectedPath) {
		return new PathMatcher(expectedPath);
	}

	public void describeTo(Description description) {
		description.appendText("a path " + expectedPath);
	}

	@Override
	protected boolean matchesSafely(IPath path, Description description) {
		return path.toString().equals(expectedPath);
	}
	
}