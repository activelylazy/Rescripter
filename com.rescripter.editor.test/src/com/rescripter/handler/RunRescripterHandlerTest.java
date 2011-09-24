package com.rescripter.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.script.RunScript;

public class RunRescripterHandlerTest {

	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	
	@Test public void
	runs_script_from_current_editor() throws ExecutionException {
		final RunScript runScript = context.mock(RunScript.class);
		final IFile file = context.mock(IFile.class);
		final IWorkbenchWindow window = context.mock(IWorkbenchWindow.class);
		final ITextEditor editor = context.mock(ITextEditor.class);
		final IFileEditorInput editorInput = context.mock(IFileEditorInput.class);
		final IDocumentProvider documentProvider = context.mock(IDocumentProvider.class);
		final IDocument document = context.mock(IDocument.class);
		
		ExecutionEvent event = new ExecutionEvent();
		final String contents = "contents";
		final String filename = "filename";
		
		context.checking(new Expectations() {{
			oneOf(editor).getEditorInput(); will(returnValue(editorInput));
			oneOf(editor).getDocumentProvider(); will(returnValue(documentProvider));
			oneOf(documentProvider).getDocument(with(editorInput)); will(returnValue(document));
			oneOf(editorInput).getFile(); will(returnValue(file));
			oneOf(document).get(); will(returnValue(contents));
			oneOf(editor).getTitle(); will(returnValue(filename));
			
			oneOf(runScript).withContents(contents, file, filename);
		}});
		
		RunRescripterHandler handler = new RunRescripterHandler() {
			@Override protected IWorkbenchWindow getWindow(ExecutionEvent event) throws ExecutionException {
				return window;
			}
			@Override protected ITextEditor getEditor() {
				return editor;
			}
			@Override protected RunScript createRunScript(IWorkbenchWindow window) {
				return runScript;
			}
		};
		handler.execute(event);
		
		context.assertIsSatisfied();
	}
}
