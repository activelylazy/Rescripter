package com.rescripter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Test;

import com.rescripter.script.RunScript;

public class FindIntegrationTest extends BaseRescripterIntegrationTest {
	@Test public void
	finds_types_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents("var person = Find.typeByName('Person');\n", null, "inline script");
		IType type = runScript.getProperty(IType.class, "person");
		assertThat(type, is(notNullValue()));
		assertThat(type, is(getJavaProject().findType("com.example.Person")));
	}
	
	@Test(expected=Exception.class) public void
	fails_to_find_missing_type_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents("var person = Find.typeByName('NotAPerson');\n", null, "inline script");
	}

	@Test public void
	finds_method_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents(
				"var person = Find.typeByName('Person');\n" +
				"var getName = Find.methodByName(person, 'getName');\n", null, "inline script");
		assertThat(runScript.getProperty(IMethod.class, "getName"), is(notNullValue()));
	}

	@Test(expected=Exception.class) public void
	fails_to_find_missing_method_by_name() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents(
				"var person = Find.typeByName('Person');\n" +
				"var getName = Find.methodByName(person, 'noSuchGetName');\n", null, "inline script");
	}
}
