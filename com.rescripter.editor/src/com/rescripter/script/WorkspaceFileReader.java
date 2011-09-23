package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class WorkspaceFileReader {

	public String getContents(IFile file) throws IOException, CoreException {
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

        return buff.toString();
	}
}
