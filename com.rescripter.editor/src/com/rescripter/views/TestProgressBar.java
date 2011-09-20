package com.rescripter.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TestProgressBar extends Canvas {

	private int maximum = 100;
	private int current = 0;
	private boolean success = true;
	
	public TestProgressBar(Composite parent) {
		super(parent, SWT.NO_FOCUS | SWT.BORDER);
		
		final Color green = new Color(null, 0, 128, 0);
		final Color red = new Color(null, 128, 0, 0);
		
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				green.dispose();
				red.dispose();
			}
		});

		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				
				gc.setBackground(success ? green : red);
				gc.fillRectangle(0, 0, getFillWidth(), getBounds().height);
			}
		});
	}
	
	private int getFillWidth() {
		if (maximum == 0) {
			return 0;
		}
		return current * getBounds().width / maximum;
	}

	public void setMaximum(int maximum) { 
		this.maximum = maximum;
		this.redraw();
	}
	
	public void setCurrent(int current) { 
		this.current = current;
		this.redraw();
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
		this.redraw();
	}
	
	
}
