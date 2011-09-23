package com.rescripter.resources;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

import com.rescripter.script.ScriptRunner;
import com.rescripter.script.ScriptStack;

public class ClasspathScriptLoader implements ScriptLoader {

	private final ScriptRunner scriptRunner;
	private final ScriptStack scriptStack;
	private final FileContentsReader fileReader;

	public ClasspathScriptLoader(ScriptRunner scriptRunner, 
			 					 ScriptStack scriptStack,
			 					 FileContentsReader fileReader) {
		this.scriptRunner = scriptRunner;
		this.scriptStack = scriptStack;
		this.fileReader = fileReader;
	}
	
	public void file(String filename) throws IOException, CoreException {
        String contents = fileReader.getContents(getClass().getClassLoader().getResourceAsStream(filename));

    	scriptStack.push(new ClasspathScriptLoader(scriptRunner, scriptStack, fileReader));
        scriptRunner.run(contents, filename);
        scriptStack.pop();
	}

}
