package com.rescripter.test.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import com.rescripter.test.matchers.PathMatcher;


public class MockContainerBuilder {
	private Mockery context = new Mockery();
	private IContainer container = context.mock(IContainer.class);
	private String name;
	private Map<String, IFile> theFiles = new HashMap<String, IFile>();
	private MockContainerBuilder parent;
	
	private MockContainerBuilder(String name) {
		this.name = name;
		context.checking(new Expectations() {{
			allowing(container).getFullPath(); will(new Action() {
				public Object invoke(Invocation invocation) {
					return new Path(getFullPath());
				}
				
				public void describeTo(Description description) {
					description.appendText("the result of lazily calling getFullPath()");
				}
			});
		}});
	}
	
	public static MockContainerBuilder a_container() {
		return new MockContainerBuilder("");
	}
	
	public static MockContainerBuilder a_container(String named) {
		return new MockContainerBuilder(named);
	}
	
	public MockContainerBuilder containing_the_file(final MockFileBuilder file) {
		file.in_container(container);
		final IFile theFile = file.build();
		theFiles.put(getNestedName(file.getPath().toString()), theFile);
		context.checking(new Expectations() {{
			allowing(container).getFile(file.getPath()); will(returnValue(theFile));
		}});
		return this;
	}
	
	public MockContainerBuilder containing(MockContainerBuilder containerBuilder) {
		containerBuilder.setParent(this);
		addNestedFiles(containerBuilder);
		return this;
	}
	
	public IContainer build() {
		return container;
	}
	
	private void setParent(MockContainerBuilder parent) {
		this.parent = parent;
	}
	
	String getFullPath() {
		if (parent == null) {
			return name;
		}
		return parent.getFullPath() + "/" + name;
	}

	private void addNestedFiles(final MockContainerBuilder otherContainer) {
		for(final Map.Entry<String, IFile> entry : otherContainer.theFiles.entrySet()) {
			final String childName = entry.getKey();
			context.checking(new Expectations() {{
				allowing(container).getFile(with(PathMatcher.a_path_matching(childName)));
					will(returnValue(entry.getValue()));
			}});
			theFiles.put(getNestedName(childName), entry.getValue());
		}
	}

	private String getNestedName(String childName) {
		return name == null ? childName
						    : name + "/" + childName;
	}
	
}