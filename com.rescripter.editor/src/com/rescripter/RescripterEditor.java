package com.rescripter;

import org.eclipse.ui.editors.text.TextEditor;

public class RescripterEditor extends TextEditor {

	private final ColorManager colorManager;

	public RescripterEditor() {
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new RSConfiguration(colorManager));
		setDocumentProvider(new RSDocumentProvider());
	}
	
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
