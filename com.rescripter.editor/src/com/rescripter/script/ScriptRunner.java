package com.rescripter.script;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchWindow;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.rescripter.resources.ClasspathScriptLoader;
import com.rescripter.resources.FileContentsReader;
import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;

public class ScriptRunner {

   private final Context context;
   private final Scriptable scope;
   private final DebugMessage debugMessage;
   private final ScriptStack scriptStack;
   private final FileContentsReader fileReader;

   public ScriptRunner(IWorkbenchWindow window, ScriptStack scriptStack, FileContentsReader fileReader) throws IOException {
      this.scriptStack = scriptStack;
      this.fileReader = fileReader;
      context = createContext();
      scope = context.initStandardObjects();
      putProperty("Load", scriptStack);
      if (Platform.isRunning()) {
         putProperty("Debug", debugMessage = new DebugMessage());
      } else {
        debugMessage = null;
      }
      putProperty("TestResult", new TestResultPublisher());
      putProperty("Alert", new Alerter(window));
      putProperty("SearchHelper", new SearchHelper());
      putProperty("ChangeText", new ChangeText());
      putProperty("ASTTokenFinder", new ASTTokenFinder());

      includeSystem();
   }

   private Context createContext() {
      return Context.enter();
   }

   public void run(String source, String sourceName) {
      context.evaluateString(scope, source, sourceName, 1, null);
   }

   public final void putProperty(String name, Object object) {
      ScriptableObject.putProperty(scope, name, object);
   }

   public Object getProperty(String name) {
      return ScriptableObject.getProperty(scope, name);
   }

   private void includeSystem() throws IOException {
      ClasspathScriptLoader loader = new ClasspathScriptLoader(this, scriptStack, fileReader);
      loader.file("System.rs");
   }

   public void done() {
      if (debugMessage != null) {
         debugMessage.done();
      }
   }
}
