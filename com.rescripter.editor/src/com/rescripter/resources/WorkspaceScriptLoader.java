package com.rescripter.resources;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.rescripter.script.ScriptRunner;
import com.rescripter.script.ScriptStack;

public class WorkspaceScriptLoader implements ScriptLoader {

    private final ScriptRunner scriptRunner;
    private final IFile location;
    private final ScriptStack scriptStack;
    private final WorkspaceFileReader fileReader;

    public WorkspaceScriptLoader(IFile location, 
    							 ScriptRunner scriptRunner, 
    							 ScriptStack scriptStack,
    							 WorkspaceFileReader fileReader) {
    	this.location = location;
        this.scriptRunner = scriptRunner;
		this.scriptStack = scriptStack;
		this.fileReader = fileReader;
    }
    
    public void file(String filename) throws IOException, CoreException {
        IContainer container = location.getParent();
        IFile file = container.getFile(new Path(filename.trim()));
        if (!file.exists()) {
            throw new IOException("Failed to find file '" + file.getLocation().toString()+"'");
        }

        String contents = fileReader.getContents(file);
        
        scriptStack.push(new WorkspaceScriptLoader(file, scriptRunner, scriptStack, fileReader));
        scriptRunner.run(contents, file.getFullPath().toPortableString(), file);
        scriptStack.pop();
    }

	public IFile getCurrentLocation() {
		return location;
	}

	public String toString() {
		return "a workspace script loader";
	}
}
