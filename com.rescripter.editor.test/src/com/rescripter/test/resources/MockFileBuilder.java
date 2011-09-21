package com.rescripter.test.resources;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class MockFileBuilder {
	private Mockery context = new Mockery();
	
	private final IPath path;
	private boolean exists = true;
	private String contents = "";
	private IContainer container = null;
	
	private MockFileBuilder(Path path) {
		this.path = path;
	}
	
	public static MockFileBuilder a_file_at(Path path) {
		return new MockFileBuilder(path);
	}
	
	public MockFileBuilder that_does_not_exist() {
		this.exists = false;
		return this;
	}
	
	public MockFileBuilder with_contents(String theContents) {
		this.contents = theContents;
		return this;
	}
	
	public IFile build() {
		final IFile file = context.mock(IFile.class);
		try {
			context.checking(new Expectations() {{
				allowing(file).exists(); will(returnValue(exists));
				allowing(file).getContents(); will(returnValue(new ByteArrayInputStream(contents.getBytes())));
				allowing(file).getParent(); will(returnValue(container));
				allowing(file).getFullPath(); will(new Action() {
					public Object invoke(Invocation invocation) {
						return new Path(getFullPath());
					}
					
					public void describeTo(Description description) {
						description.appendText("return the full path");
					}
				});
				allowing(file).getLocation(); will(returnValue(path));
			}});
		} catch (CoreException e) {
			// Nonsensical exception, we're declaring expectations
		}
		return file;
	}
	
	MockFileBuilder in_container(IContainer theContainer) {
		this.container = theContainer;
		return this;
	}
	
	IPath getPath() {
		return this.path;
	}
	
	String getFullPath() {
		return container.getFullPath() == null 
				? path.toString()
				: container.getFullPath() + File.separator + path.toString();
	}
	
}