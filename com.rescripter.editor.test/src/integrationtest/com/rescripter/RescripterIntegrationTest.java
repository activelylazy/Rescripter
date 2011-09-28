package com.rescripter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

import com.rescripter.script.RunScript;

public class RescripterIntegrationTest extends BaseRescripterIntegrationTest {

	@Test public void
	populates_java_project() throws JavaModelException {
		assertThat(getJavaProject().findType("com.example.Person"), is(notNullValue()));
	}
	
	@Test public void
	runs_basic_script() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents("var response = 42;\n", null, "inline script");
		assertThat((Integer) runScript.getProperty("response"), is(42));
	}
	
}
