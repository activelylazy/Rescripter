package com.rescripter;

import org.eclipse.ui.editors.text.TextEditor;

public class RescripterEditor extends TextEditor {

	private ColorManager colorManager;

	public RescripterEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new RSConfiguration(colorManager));
		setDocumentProvider(new RSDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
