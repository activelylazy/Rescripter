package com.rescripter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.rescripter.script.RunScript;

public class FindIntegrationTest extends BaseRescripterIntegrationTest {
	@Test public void
	finds_types_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents("var person = Find.typeByName('Person');\n", null, "inline script");
		assertThat(runScript.getProperty("person"), is(notNullValue()));
	}
	
	@Test(expected=Exception.class) public void
	fails_to_find_missing_type_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents("var person = Find.typeByName('NotAPerson');\n", null, "inline script");
	}

}
