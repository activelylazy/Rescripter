package com.rescripter.script;

import org.eclipse.ui.IWorkbenchWindow;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;
import com.rescripter.syntax.JavaSyntax;

public class ScriptRunner {

    private Context context;
    private Scriptable scope;

    ScriptRunner() {
        context = Context.enter();
        scope = context.initStandardObjects();
    }

    public void run(String source, String sourceName) {
        context.evaluateString(scope, source, sourceName, 1, null);
    }

    public void putProperty(String name, Object object) {
        ScriptableObject.putProperty(scope, name, object);
    }
    
    public static ScriptRunner createJavaSyntaxScriptRunner(IWorkbenchWindow window) {
        Alerter alerter = new Alerter(window);
        
        ScriptRunner runner = new ScriptRunner();
        runner.putProperty("Java", new JavaSyntax());
        runner.putProperty("Alert", alerter);
        runner.putProperty("Find", new Find());
        runner.putProperty("ChangeText", new ChangeText());
        runner.putProperty("ASTTokenFinder", new ASTTokenFinder());
        return runner;
    }
}
