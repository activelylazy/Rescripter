package com.rescripter.script;

import static com.rescripter.test.resources.MockContainerBuilder.a_container;
import static com.rescripter.test.resources.MockFileBuilder.a_file_at;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class ScriptLoaderTest {
	
	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};

	@Test public void 
	loads_and_runs_a_single_script() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		
		final String source = "the source, Luke\n";
		
		final IContainer currentDirectory = a_container()
												.containing_the_file(a_file_at(new Path("current_file.js")))
												.containing_the_file(a_file_at(new Path("some_file.js"))
																		.with_contents(source))
												.build();
		
		context.checking(new Expectations() {{
			oneOf(runner).run(with(source), with("/some_file.js"), with(currentDirectory.getFile(new Path("some_file.js"))));
		}});
		
		ScriptLoader loader = new ScriptLoader(runner);
		loader.setCurrentLocation(currentDirectory.getFile(new Path("current_file.js")));
		loader.file("some_file.js");
		
		context.assertIsSatisfied();
	}
	
	@Test(expected=IOException.class) public void
	throws_io_exception_if_file_not_found() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		
		final String currentFile = "current_file.js";
		final String someFile = "some_file.js";

		final IContainer currentDirectory = a_container()
				.containing_the_file(a_file_at(new Path(currentFile)))
				.containing_the_file(a_file_at(new Path(someFile))
										.that_does_not_exist())
				.build();
		
		
		ScriptLoader loader = new ScriptLoader(runner);
		loader.setCurrentLocation(currentDirectory.getFile(new Path(currentFile)));
		
		loader.file(someFile);
	}
	
	@Test public void
	loads_nested_files() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		
		final String currentFile = "current_file.js";
		final String fullPathToFirstFile = "/some/file.js";
		final String firstFile = "some/file.js";
		final String firstContents = "I am your father\n";
		
		final String fullPathToReferencedFile = "/some/other/library.js";
		final String relativePathToReferencedFile = "other/library.js";
		final String referencedContents = "nothing to see here\n";
		
		final IContainer currentDirectory = a_container()
												.containing_the_file(a_file_at(new Path(currentFile)))
												.containing(
													a_container("some")
														.containing_the_file(a_file_at(new Path("file.js"))
																.with_contents(firstContents))
														.containing(
															a_container("other")
																.containing_the_file(a_file_at(new Path("library.js"))
																	.with_contents(referencedContents))))
												.build();

		final ScriptLoader loader = new ScriptLoader(runner);
		
		context.checking(new Expectations() {{
			oneOf(runner).run(firstContents, fullPathToFirstFile, currentDirectory.getFile(new Path(firstFile)));
				will(new Action() {
					public void describeTo(Description description) {
						description.appendText("will make a nested call to ScriptLoader to load the referenced file");
					}
	
					public Object invoke(Invocation invocation) throws Throwable {
						loader.file(relativePathToReferencedFile);
						return null;
					}
				});
			
			oneOf(runner).run(referencedContents, fullPathToReferencedFile, currentDirectory.getFile(new Path("some/other/library.js")));
		}});
		
		loader.setCurrentLocation(currentDirectory.getFile(new Path("current_file.js")));
		loader.file(firstFile);
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	loads_multiple_files() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		
		final String currentFile = "current_file.js";
		
		final String fullPathToFirstFile = "/some/file.js";
		final String firstFile = "some/file.js";
		final String firstContents = "I am your father\n";
		
		final String fullPathToSecondFile = "/another/utility.js";
		final String secondFile = "another/utility.js";
		final String secondContents = "nothing to see here\n";
		
		final IContainer currentDirectory = a_container()
												.containing_the_file(a_file_at(new Path(currentFile)))
												.containing(
													a_container("some")
														.containing_the_file(a_file_at(new Path("file.js"))
																.with_contents(firstContents)))
												.containing(
													a_container("another")
														.containing_the_file(a_file_at(new Path("utility.js"))
															.with_contents(secondContents)))
												.build();

		final ScriptLoader loader = new ScriptLoader(runner);
		
		context.checking(new Expectations() {{
			oneOf(runner).run(firstContents, fullPathToFirstFile, currentDirectory.getFile(new Path(firstFile)));
			oneOf(runner).run(secondContents, fullPathToSecondFile, currentDirectory.getFile(new Path(secondFile)));
		}});
		
		loader.setCurrentLocation(currentDirectory.getFile(new Path("current_file.js")));
		loader.file(firstFile);
		loader.file(secondFile);
		
		context.assertIsSatisfied();
	}
	
}
