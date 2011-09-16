package com.rescripter.test.resources;

import org.eclipse.core.resources.IContainer;
import org.jmock.Expectations;
import org.jmock.Mockery;


public class MockContainerBuilder {
	private Mockery context = new Mockery();
	private IContainer container = context.mock(IContainer.class);
	
	private MockContainerBuilder() { }
	
	public static MockContainerBuilder a_container() {
		return new MockContainerBuilder();
	}
	
	public MockContainerBuilder containing_the_file(final MockFileBuilder file) {
		file.in_container(container);
		context.checking(new Expectations() {{
			allowing(container).getFile(file.getPath()); will(returnValue(file.build()));
		}});
		return this;
	}
	
	public IContainer build() {
		return container;
	}
	
}