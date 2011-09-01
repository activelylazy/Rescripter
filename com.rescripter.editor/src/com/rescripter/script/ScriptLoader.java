package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class ScriptLoader {

    private final ScriptRunner scriptRunner;
    private IFile location;

    public ScriptLoader(ScriptRunner scriptRunner) {
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
        scriptRunner.run(buff.toString(), file.getFullPath().toOSString(), file);
    }

    public void setCurrentLocation(IFile location) {
        this.location = location;
    }
}
