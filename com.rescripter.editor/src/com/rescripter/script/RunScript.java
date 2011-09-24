package com.rescripter.script;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;

import com.rescripter.resources.FileContentsReader;
import com.rescripter.resources.WorkspaceScriptLoader;

public class RunScript {

	private final ScriptStack scriptStack = new ScriptStack();
	private final FileContentsReader fileReader = new FileContentsReader();
	private ScriptRunner runner;
	
	public RunScript(IWorkbenchWindow window) throws IOException, CoreException {
		this.runner = new ScriptRunner(window, scriptStack, fileReader);
	}
	
	public void withContents(String contents, IFile file, String filename) {
		try {
	    	WorkspaceScriptLoader loader = new WorkspaceScriptLoader(file, runner, scriptStack, fileReader);
			scriptStack.push(loader);
			runner.run(contents, filename);
		} finally {
			runner.done();
		}
	}

	void setScriptRunner(ScriptRunner runner) {
		this.runner = runner;
	}
}
