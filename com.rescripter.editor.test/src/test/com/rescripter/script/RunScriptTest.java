package com.rescripter.script;

import static com.rescripter.test.matchers.WorkspaceScriptLoaderMatcher.a_loader_relative_to;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchWindow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class RunScriptTest {

	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	
	@Test public void
	runs_script() throws IOException, CoreException {
		final IWorkbenchWindow window = context.mock(IWorkbenchWindow.class);
		final IFile file = context.mock(IFile.class);
		final ScriptRunner scriptRunner = context.mock(ScriptRunner.class);
		final ScriptStack scriptStack = context.mock(ScriptStack.class);
		
		final String contents = "contents";
		final String filename = "filename";
		final Path fullPath = new Path("full path");
		
		context.checking(new Expectations() {{
			allowing(file).getFullPath(); will(returnValue(fullPath));
			
			oneOf(scriptStack).push(with(a_loader_relative_to(fullPath.toPortableString())));
			oneOf(scriptRunner).run(contents, filename);
			oneOf(scriptStack).pop();
			oneOf(scriptRunner).done();
		}});
		
		RunScript runScript = new RunScript(window);
		runScript.setScriptRunner(scriptRunner);
		runScript.setScriptStack(scriptStack);
		
		runScript.withContents(contents, file, filename);
		
		context.assertIsSatisfied();
	}
	
}
