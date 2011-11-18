package com.rescripter.views;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.rescripter.script.TestResult;

final class TestResultLabelProvider implements ILabelProvider, IColorProvider {

	private final Color red;
	private final Color green;

	public TestResultLabelProvider() {
		this.red = new Color(null, 128, 0, 0);
		this.green = new Color(null, 0, 128, 0);
	}
	
	public void addListener(ILabelProviderListener listener) { }

	public void dispose() {
		red.dispose();
		green.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return ((TestResult)element).getLabel();
	}

	public Color getForeground(Object element) {
		return ((TestResult)element).isSuccess() ? green : red;
	}

	public Color getBackground(Object element) {
		return null;
	}

}