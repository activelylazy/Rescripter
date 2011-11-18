package com.rescripter.resources;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

public interface ScriptLoader {

	void file(String filename) throws IOException, CoreException;
	
}
