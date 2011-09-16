package com.rescripter.script;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.rescripter.views.TestResultView;

public class TestResultPublisher {
	
	private static class Result {
		String suite;
		String spec;
		String message;
		public Result(String suite, String spec, String message) {
			super();
			this.suite = suite;
			this.spec = spec;
			this.message = message;
		}
	}
	
	private List<Result> results = new ArrayList<Result>();

	public synchronized void startTest() throws PartInitException {
		results.clear();
		getTestResultView().startTest();
		
	}
	public synchronized void queueResult(String suite, String spec, String message) {
		results.add(new Result(suite, spec, message));
	}
	
	public synchronized void updateResults() throws PartInitException {
		for(Result result : results) {
			getTestResultView().reportResult(result.suite, result.spec, result.message);
		}
		results.clear();
	}
	private TestResultView getTestResultView() throws PartInitException {
		return (TestResultView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.rescripter.views.testresultview");
	}
}
