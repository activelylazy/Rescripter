package com.rescripter.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.rescripter.script.TestResult;

public class TestResultView extends ViewPart {

	private Text text;
	private Composite panel;
	private TestProgressBar progress;
	
	private java.util.List<TestResult> testResultList;
	private TreeViewer treeViewer;
	
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
		
		progress = new TestProgressBar(panel);
		progress.setMaximum(0);
		progress.setCurrent(0);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 20;
		progress.setLayoutData(gridData);

		testResultList = new ArrayList<TestResult>();
		
		treeViewer = new TreeViewer(panel);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL) ;
		treeViewer.getTree().setLayoutData(gridData);
		
		treeViewer.setContentProvider(new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() { }
			
			public boolean hasChildren(Object element) {
				return false;
			}
			
			public Object getParent(Object element) {
				return null;
			}
			
			public Object[] getElements(Object inputElement) {
				return testResultList.toArray();
			}
			
			public Object[] getChildren(Object parentElement) {
				return new Object[0];
			}
		});
		treeViewer.setLabelProvider(new TestResultLabelProvider());
		treeViewer.setInput("root");
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
				TestResult testResult = (TestResult) selection.getFirstElement();
				if (testResult == null) {
					text.setText("");
					return;
				}
				text.setText(testResult.getMessage());
			}
		});
		
		label = new Label(panel, 0);
		label.setText("Details");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);

		text = new Text(panel, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		text.setText("");
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL) ;
		text.setLayoutData(gridData);
	}
	
	@Override
	public void setFocus() {
	}
	
	public void startTest(int numSpecs) {
		testResultList.clear();
		progress.setMaximum(numSpecs);
		progress.setCurrent(0);
		progress.setSuccess(true);
		panel.redraw();
	}
	
	public void updateProgress(int numCompleted, int numErrors) {
		progress.setCurrent(numCompleted);
		progress.setSuccess(numErrors == 0);
	}

	public void reportResult(TestResult result) {
		testResultList.add(result);
		treeViewer.refresh(true);
		panel.redraw();
	}

}
