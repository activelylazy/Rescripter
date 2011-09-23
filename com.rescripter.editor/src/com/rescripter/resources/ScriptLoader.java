package com.rescripter.resources;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

public interface ScriptLoader {

	public void file(String filename) throws IOException, CoreException;
	
}
