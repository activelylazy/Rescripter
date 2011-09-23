package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class WorkspaceScriptLoader implements ScriptLoader {

    private final ScriptRunner scriptRunner;
    private IFile location;

    public WorkspaceScriptLoader(IFile location, ScriptRunner scriptRunner) {
    	this.location = location;
        this.scriptRunner = scriptRunner;
    }
    
    public void file(String filename) throws IOException, CoreException {
        IContainer container = location.getParent();
        IFile file = container.getFile(new Path(filename.trim()));
        if (!file.exists()) {
            throw new IOException("Failed to find file '" + file.getLocation().toString()+"'");
        }
        
        StringBuffer buff = new StringBuffer();
        LineNumberReader in = new LineNumberReader(new InputStreamReader(file.getContents()));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                buff.append(line).append("\n");
            }
        } finally {
            in.close();
        }
        IFile lastLocation = location;
        this.location = file;
        scriptRunner.run(buff.toString(), file.getFullPath().toOSString(), file);
        this.location = lastLocation;
    }

	public IFile getCurrentLocation() {
		return this.location;
	}

}
