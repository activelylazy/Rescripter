package com.rescripter.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.HandlerUtil;

import com.rescripter.script.ScriptRunner;

public class RunRescripterHandler extends AbstractHandler {
	public RunRescripterHandler() { }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		TextEditor editor = (TextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		ScriptRunner runner = ScriptRunner.createJavaSyntaxScriptRunner(window);
		runner.run(document.get(), editor.getTitle());
		
		return null;
	}

}
