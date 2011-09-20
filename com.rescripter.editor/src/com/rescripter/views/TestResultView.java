package com.rescripter.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class TestResultView extends ViewPart {

	private Text text;
	private List testList;
	private Composite panel;
	
	private Map<String, String> messages = new HashMap<String, String>();
	private ProgressBar progress;
	
	@Override
	public void createPartControl(Composite parent) {
		panel = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		panel.setLayout(layout);
		
		Label label = new Label(panel, 0);
		label.setText("Tests");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);
		
		progress = new ProgressBar(panel, SWT.HORIZONTAL);
		progress.setMaximum(0);
		progress.setSelection(0);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		progress.setLayoutData(gridData);

		testList = new List(panel, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL) ;
		testList.setLayoutData(gridData);
		
		label = new Label(panel, 0);
		label.setText("Details");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);

		text = new Text(panel, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		text.setText("");
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL) ;
		text.setLayoutData(gridData);
		
		hookClickAction();
	}
	
	private String getMessage(String key) {
		return messages.get(key);
	}
	
	private void hookClickAction() {
		testList.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				text.setText(getMessage(testList.getSelection()[0]));
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
	}

	@Override
	public void setFocus() {
	}
	
	public void startTest(int numSpecs) {
		testList.removeAll();
		progress.setMaximum(numSpecs);
		progress.setSelection(0);
		panel.redraw();
	}
	
	public void updateProgress(int numCompleted) {
		progress.setSelection(numCompleted);
	}

	public void reportResult(String suite, String spec, String message) {
		testList.add(suite + " " + spec);
		messages.put(suite + " " + spec, message);
		panel.redraw();
	}

}
