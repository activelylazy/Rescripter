package com.rescripter.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchWindow;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;

public class ScriptRunner {

    private Context context;
    private Scriptable scope;
    private ScriptLoader scriptLoader;

    public ScriptRunner() {
        context = Context.enter();
        scope = context.initStandardObjects();
        scriptLoader = new ScriptLoader(this);
        putProperty("Load", scriptLoader);
    }

    public void run(String source, String sourceName, IFile location) {
        scriptLoader.setCurrentLocation(location);
        context.evaluateString(scope, source, sourceName, 1, null);
    }
    
    public void putProperty(String name, Object object) {
        ScriptableObject.putProperty(scope, name, object);
    }
    
    public static ScriptRunner createJavaSyntaxScriptRunner(IWorkbenchWindow window) throws IOException {
        Alerter alerter = new Alerter(window);
        
        ScriptRunner runner = new ScriptRunner();
        runner.putProperty("Alert", alerter);
        runner.putProperty("SearchHelper", new SearchHelper());
        runner.putProperty("ChangeText", new ChangeText());
        runner.putProperty("ASTTokenFinder", new ASTTokenFinder());
        runner.putProperty("Debug", new DebugMessage());
        
        runner.includeSystem();
        
        return runner;
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
}
