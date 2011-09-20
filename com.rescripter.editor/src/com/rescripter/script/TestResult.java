package com.rescripter.script;

public class TestResult {
	private String suite;
	private String spec;
	private String message;
	private boolean success;
	
	public TestResult(String suite, String spec, String message, boolean success) {
		super();
		this.suite = suite;
		this.spec = spec;
		this.message = message;
		this.success = success;
	}

	public String getSuite() { return suite; }
	public String getSpec() { return spec; }
	public String getMessage() { return message; }
	public boolean isSuccess() { return success; }

	public String getLabel() {
		return getSuite() + " " + getSpec();
	}
}