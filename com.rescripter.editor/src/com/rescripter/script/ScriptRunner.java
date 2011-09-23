package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchWindow;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;

public class ScriptRunner {

    private final Context context;
    private final Scriptable scope;
    private final ScriptStack scriptStack;
	private DebugMessage debugMessage;

    public ScriptRunner(IWorkbenchWindow window) throws IOException {
        context = Context.enter();
        scope = context.initStandardObjects();
        putProperty("Load", scriptStack = new ScriptStack());
        if (Platform.isRunning()) {
        	putProperty("Debug", debugMessage = new DebugMessage());
        }
		putProperty("TestResult", new TestResultPublisher());
        putProperty("Alert", new Alerter(window));
        putProperty("SearchHelper", new SearchHelper());
        putProperty("ChangeText", new ChangeText());
        putProperty("ASTTokenFinder", new ASTTokenFinder());

        includeSystem();
    }

    public void run(String source, String sourceName, IFile location) {
    	WorkspaceScriptLoader loader = new WorkspaceScriptLoader(location, this);
		scriptStack.push(loader);
        context.evaluateString(scope, source, sourceName, 1, null);
    }

    public void putProperty(String name, Object object) {
        ScriptableObject.putProperty(scope, name, object);
    }
    
    public Object getProperty(String name) {
    	return ScriptableObject.getProperty(scope, name);
    }

    private void includeSystem() throws IOException {
    	StringBuffer buff = new StringBuffer();
    	LineNumberReader in = new LineNumberReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("System.rs")));
    	try {
    		String line;
    		while ((line = in.readLine()) != null) {
    			buff.append(line).append("\n");
    		}
    	} finally {
    		in.close();
    	}
    	run(buff.toString(), "System.rs", null);
    }

	public void done() {
		debugMessage.done();
	}
}
