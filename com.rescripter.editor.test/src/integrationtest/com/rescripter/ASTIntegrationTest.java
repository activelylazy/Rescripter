package com.rescripter;

import static com.rescripter.ASTIntegrationTest.ASTNodeMatcher.a_node;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

import com.rescripter.script.RunScript;

public class ASTIntegrationTest extends BaseRescripterIntegrationTest {
	
	@Test public void
	parses_compilation_unit() throws IOException {
		RunScript runScript = new RunScript(getWindow());
		runScript.withContents(
		    "var person = Find.typeByName('Person');\n" + 
		    "var ast = AST.parseCompilationUnit(person.getCompilationUnit());\n", null, "inline script");
		
		ASTNode ast = runScript.getProperty(ASTNode.class, "ast");
		
		assertThat(ast, is(notNullValue()));
		assertThat(ast.toString(), containsString("public class Person"));
		assertThat(ast.toString(), containsString("public Person("));
	}

	@SuppressWarnings("unchecked")
	@Test public void
	finds_covered_node() throws IOException, CoreException {
		RunScript runScript = new RunScript(getWindow());
		String methodCallText = "person.setName(\"Bob\")";
		IType personType = getJavaProject().findType("com.example.Person");
		String personSource = personType.getCompilationUnit().getSource();
		int offsetOfCall = personSource.indexOf(methodCallText);
		runScript.withContents(
		    "var person = Find.typeByName('Person');\n" + 
		    "var ast = AST.parseCompilationUnit(person.getCompilationUnit());\n" +
		    "var node = AST.findCoveredNode(ast, " + offsetOfCall + ", " + methodCallText.length() + ");\n"
		    , null, "inline script");

		MethodInvocation node = runScript.getProperty(MethodInvocation.class, "node");
		
		assertThat(node, is(notNullValue()));
		assertThat(node.getExpression().toString(), is("person"));
		assertThat(node.getName().toString(), is("setName"));
		assertThat((List<ASTNode>) node.arguments(), 
				   Matchers.<ASTNode>hasItems(a_node().with_text_representation("\"Bob\"")));
	}
	
	public static final class ASTNodeMatcher extends TypeSafeDiagnosingMatcher<ASTNode> {
		public static final ASTNodeMatcher a_node() {
			return new ASTNodeMatcher();
		}

		private String text;
		
		private ASTNodeMatcher() { }
		
		public ASTNodeMatcher with_text_representation(String textRepresentation) {
			this.text = textRepresentation;
			return this;
		}

		public void describeTo(Description description) {
			description.appendText("an ASTNode");
			if (text != null) {
				description.appendText(" with text representation '" + text + "'");
			}
		}

		@Override
		protected boolean matchesSafely(ASTNode node, Description description) {
			description.appendText("an ASTNode");
			if (text != null && !node.toString().equals(text)) {
				description.appendText(" with text representation '" + node.toString() + "'");
				return false;
			}
			return true;
		}
	}
}
