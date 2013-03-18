package com.rescripter.script;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.rescripter.views.TestResultView;

public class TestResultPublisher {
	
	private int numSpecsStarted;
	private int numSpecsInError;
	
	private List<TestResult> results = new ArrayList<TestResult>();

	public synchronized void startTest(int numSpecs) throws PartInitException {
		results.clear();
		getTestResultView().startTest(numSpecs);
		numSpecsStarted = 0;
		numSpecsInError = 0;
	}
	
	public synchronized void specStarted() {
		numSpecsStarted++;
	}
	
	public synchronized void queueResult(String suite, String spec, String message, boolean success) {
		results.add(new TestResult(suite, spec, message, success));
	}
	
	public synchronized void updateResults() throws PartInitException {
		for(TestResult result : results) {
			getTestResultView().reportResult(result);
			if (!result.isSuccess()) {
				numSpecsInError++;
			}
		}
		getTestResultView().updateProgress(numSpecsStarted, numSpecsInError);
		results.clear();
	}
	private TestResultView getTestResultView() throws PartInitException {
		return (TestResultView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.rescripter.views.testresultview");
	}
}
