package com.rescripter.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import com.rescripter.script.Alerter;
import com.rescripter.script.ScriptRunner;

public class RunRescripterHandler extends AbstractHandler {
	public RunRescripterHandler() { }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			TextEditor editor = (TextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			IEditorInput editorInput = editor.getEditorInput();
            IDocument document = editor.getDocumentProvider().getDocument(editorInput);
            
            FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
            IFile file = fileEditorInput.getFile();
			
			ScriptRunner runner = new ScriptRunner(window);
			try {
				runner.run(document.get(), editor.getTitle(), file);
			} finally {
				runner.done();
			}
			
			return null;
		} catch (Throwable t) {
			t.printStackTrace();
			new Alerter(window).error("Error running script: "+t.getMessage());
			return null;
		}
	}

}
