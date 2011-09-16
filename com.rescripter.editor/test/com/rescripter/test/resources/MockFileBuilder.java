package com.rescripter.test.resources;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;

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
	
	public MockFileBuilder with_contents(String contents) {
		this.contents = contents;
		return this;
	}
	
	MockFileBuilder in_container(IContainer container) {
		this.container = container;
		return this;
	}
	
	IPath getPath() {
		return this.path;
	}
	
	public IFile build() {
		final IFile file = context.mock(IFile.class);
		try {
			context.checking(new Expectations() {{
				allowing(file).exists(); will(returnValue(exists));
				allowing(file).getContents(); will(returnValue(new ByteArrayInputStream(contents.getBytes())));
				allowing(file).getParent(); will(returnValue(container));
				allowing(file).getFullPath(); will(returnValue(path));
			}});
		} catch (CoreException e) {
			// Nonsensical exception, we're declaring expectations
		}
		return file;
	}
	
}