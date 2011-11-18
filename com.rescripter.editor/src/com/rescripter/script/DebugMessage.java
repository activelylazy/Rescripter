package com.rescripter.script;

import java.io.IOException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.rescripter.Activator;

public class DebugMessage {

	private MessageConsoleStream stream;
	private static final MessageConsole console = new MessageConsole("Rescripter Debug", null);
	private static final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
	
	static {
		consoleManager.addConsoles(new IConsole[] { console });
	}

	public DebugMessage() {
		consoleManager.showConsoleView(console);
		stream = console.newMessageStream();
	}
	
    public void message(String message) {
        stream.println(message);
    }
    
    public void sourceRange(ICompilationUnit cu, ISourceRange sourceRange) throws JavaModelException {
        stream.println(cu.getSource().substring(sourceRange.getOffset(), sourceRange.getOffset() + sourceRange.getLength()));
    }

	public void done() {
		try {
			stream.close();
		} catch (IOException e) {
			Activator.warn(e);
		}
	}
}
