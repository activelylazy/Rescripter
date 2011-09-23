package com.rescripter.script;

import java.io.IOException;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;

public class ScriptStack implements ScriptLoader {

	private Stack<ScriptLoader> stack = new Stack<ScriptLoader>();
	
	public void file(String filename) throws IOException, CoreException {
		peek().file(filename);
	}
	
	public void push(ScriptLoader loader) {
		stack.push(loader);
	}
	
	public void pop() {
		stack.pop();
	}
	
	private ScriptLoader peek() {
		return stack.peek();
	}
}
