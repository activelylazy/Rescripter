package com.rescripter.script;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.resources.FileContentsReader;

public class RunScriptTest {

	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	
	@Test public void
	runs_script() throws IOException, CoreException {
		final IWorkbenchWindow window = context.mock(IWorkbenchWindow.class);
		final IFile file = context.mock(IFile.class);
		final ScriptRunner scriptRunner = context.mock(ScriptRunner.class);
		
		final String contents = "contents";
		final String filename = "filename";
		
		context.checking(new Expectations() {{
			oneOf(scriptRunner).run(contents, filename);
			oneOf(scriptRunner).done();
		}});
		
		RunScript runScript = new RunScript(window) {
			@Override protected ScriptRunner createScriptRunner(ScriptStack scriptStack,
													  FileContentsReader fileReader) throws IOException, CoreException {
				return scriptRunner;
			}
			
		};
		
		runScript.withContents(contents, file, filename);
		
		context.assertIsSatisfied();
	}
}
