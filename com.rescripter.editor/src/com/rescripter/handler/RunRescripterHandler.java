package com.rescripter.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.rescripter.script.Alerter;
import com.rescripter.script.RunScript;

public class RunRescripterHandler extends AbstractHandler {
	public RunRescripterHandler() { }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = getWindow(event);
		try {
			ITextEditor editor = getEditor();
			IEditorInput editorInput = editor.getEditorInput();
            IDocument document = editor.getDocumentProvider().getDocument(editorInput);
            
            IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
            IFile file = fileEditorInput.getFile();
            
            createRunScript(window).withContents(document.get(), file, editor.getTitle());
			
			return null;
		} catch (Throwable t) {
			t.printStackTrace();
			new Alerter(window).error("Error running script: "+t.getMessage());
			return null;
		}
	}

	protected RunScript createRunScript(IWorkbenchWindow window) throws IOException, CoreException {
		return new RunScript(window);
	}

	protected ITextEditor getEditor() {
		return (TextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	protected IWorkbenchWindow getWindow(ExecutionEvent event) throws ExecutionException {
		return HandlerUtil.getActiveWorkbenchWindowChecked(event);
	}

}
