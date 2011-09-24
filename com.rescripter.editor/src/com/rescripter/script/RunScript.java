package com.rescripter.script;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;

import com.rescripter.resources.FileContentsReader;
import com.rescripter.resources.WorkspaceScriptLoader;

public class RunScript {

	private final IWorkbenchWindow window;

	public RunScript(IWorkbenchWindow window) {
		this.window = window;
	}
	
	public void withContents(String contents, IFile file, String filename) throws IOException, CoreException {
        ScriptStack scriptStack = new ScriptStack();
        FileContentsReader fileReader = new FileContentsReader();
		ScriptRunner runner = createScriptRunner(scriptStack, fileReader);
		try {
	    	WorkspaceScriptLoader loader = new WorkspaceScriptLoader(file, runner, scriptStack, fileReader);
			scriptStack.push(loader);
			runner.run(contents, filename);
		} finally {
			runner.done();
		}
	}

	protected ScriptRunner createScriptRunner(ScriptStack scriptStack,
											  FileContentsReader fileReader) throws IOException, CoreException 
	{
		return new ScriptRunner(window, scriptStack, fileReader);
	}
	
}
