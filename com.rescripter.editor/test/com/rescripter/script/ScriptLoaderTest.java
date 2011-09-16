package com.rescripter.script;

import static com.rescripter.test.resources.MockContainerBuilder.a_container;
import static com.rescripter.test.resources.MockFileBuilder.a_file_at;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.internal.ReturnDefaultValueAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.test.matchers.PathMatcher;

public class ScriptLoaderTest {
	
	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};

	@Test public void 
	loads_and_runs_a_single_script() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		
		final String currentFile = "current_file.js";
		final String someFile = "some_file.js";
		final String source = "the source, Luke\n";
		
		final IContainer currentDirectory = a_container()
												.containing_the_file(a_file_at(new Path(currentFile)))
												.containing_the_file(a_file_at(new Path(someFile))
																		.with_contents(source))
												.build();
		
		context.checking(new Expectations() {{
			oneOf(runner).run(with(source), with(someFile), with(currentDirectory.getFile(new Path(someFile))));
		}});
		
		ScriptLoader loader = new ScriptLoader(runner);
		loader.setCurrentLocation(currentDirectory.getFile(new Path(currentFile)));
		loader.file(someFile);
		
		context.assertIsSatisfied();
	}
	
	@Test(expected=IOException.class) public void
	throws_io_exception_if_file_not_found() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		final IContainer currentDirectory = context.mock(IContainer.class);
		final IFile currentLocation = context.mock(IFile.class, "currentLocation");
		final String someFileJSFilename = "some/file.js";
		final IFile someFileJS = context.mock(IFile.class, "someFileJS");
		
		context.checking(new Expectations() {{
			allowing(currentLocation).getParent(); will(returnValue(currentDirectory));
			allowing(currentDirectory).getFile(with(PathMatcher.a_path_matching(someFileJSFilename))); will(returnValue(someFileJS));
			allowing(someFileJS).exists(); will(returnValue(false));
			allowing(someFileJS).getLocation(); will(returnValue(new Path(someFileJSFilename)));
		}});
		
		ScriptLoader loader = new ScriptLoader(runner);
		loader.setCurrentLocation(currentLocation);
		
		loader.file(someFileJSFilename);
	}
	
	@Test public void
	loads_nested_files() throws IOException, CoreException {
		final ScriptRunner runner = context.mock(ScriptRunner.class);
		final IContainer startDirectory = context.mock(IContainer.class, "startDirectory");
		final IFile startLocation = context.mock(IFile.class, "currentLocation");
		
		final IContainer parentDirectory = context.mock(IContainer.class, "parentDirectory");
		final String parentFilename = "parent/filename";
		final IFile parentFile = context.mock(IFile.class, "parentFile");
		final String parentSource = "I am your father\n";
		
		final String childFilename = "../child/filename";
		final IFile childFile = context.mock(IFile.class, "childFile");
		final String childSource = "whatever\n";

		final ScriptLoader loader = new ScriptLoader(runner);
		
		context.checking(new Expectations() {{
			oneOf(startLocation).getParent(); will(returnValue(startDirectory));
			oneOf(startDirectory).getFile(with(PathMatcher.a_path_matching(parentFilename))); will(returnValue(parentFile));
			oneOf(parentFile).exists(); will(returnValue(true));
			oneOf(parentFile).getContents(); will(returnValue(new ByteArrayInputStream(parentSource.getBytes())));
			oneOf(parentFile).getFullPath(); will(returnValue(new Path(parentFilename)));
			oneOf(runner).run(parentSource, parentFilename, parentFile); will(new ReturnDefaultValueAction() {

				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					loader.file(childFilename);
					return null;
				}
				
			});
			
			oneOf(parentFile).getParent(); will(returnValue(parentDirectory));
			oneOf(parentDirectory).getFile(with(PathMatcher.a_path_matching(childFilename))); will(returnValue(childFile));
			oneOf(childFile).exists(); will(returnValue(true));
			oneOf(childFile).getContents(); will(returnValue(new ByteArrayInputStream(childSource.getBytes())));
			oneOf(childFile).getFullPath(); will(returnValue(new Path(childFilename)));
			oneOf(runner).run(childSource, childFilename, childFile);
		}});
		
		loader.setCurrentLocation(startLocation);
		loader.file(parentFilename);
		
		context.assertIsSatisfied();
	}
}
