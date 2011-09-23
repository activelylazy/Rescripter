package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

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
	private DebugMessage debugMessage;

    public ScriptRunner(IWorkbenchWindow window,
    					ScriptStack scriptStack) throws IOException {
        this.context = createContext();
        this.scope = context.initStandardObjects();
        putProperty("Load", scriptStack);
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
    
    protected Context createContext() {
    	return Context.enter();
    }

    public void run(String source, String sourceName) {
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
    	run(buff.toString(), "System.rs");
    }

	public void done() {
		debugMessage.done();
	}
}
