package com.rescripter.script;

import java.io.IOException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DebugMessage {

	private MessageConsoleStream stream;
	private static MessageConsole console = new MessageConsole("Rescripter Debug", null);
	
	static {
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
	}

	public DebugMessage() {
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
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
			e.printStackTrace();
		}
	}
}
