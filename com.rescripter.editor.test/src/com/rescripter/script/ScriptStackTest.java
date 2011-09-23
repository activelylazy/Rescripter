package com.rescripter.script;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Test;

public class ScriptStackTest {

	private Mockery context = new Mockery();
	
	@Test public void
	delegates_to_pushed_loader() throws IOException, CoreException {
		ScriptStack stack = new ScriptStack();
		final ScriptLoader loader = context.mock(ScriptLoader.class);
		final String filename = "foo.rs";
		
		context.checking(new Expectations() {{
			oneOf(loader).file(filename);
		}});
		
		stack.push(loader);
		stack.file(filename);
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	pushes_and_pops_loaders() throws IOException, CoreException {
		ScriptStack stack = new ScriptStack();
		final ScriptLoader loader1 = context.mock(ScriptLoader.class, "loader1");
		final ScriptLoader loader2 = context.mock(ScriptLoader.class, "loader2");
		final Sequence sequence = context.sequence("loaderSequence");
		final String filename = "foo.rs";
		
		context.checking(new Expectations() {{
			oneOf(loader1).file(filename); inSequence(sequence);
			oneOf(loader2).file(filename); inSequence(sequence);
			oneOf(loader1).file(filename); inSequence(sequence);
		}});
		
		stack.push(loader1);
		stack.file(filename);
		stack.push(loader2);
		stack.file(filename);
		stack.pop();
		stack.file(filename);
		
		context.assertIsSatisfied();
	}
}
