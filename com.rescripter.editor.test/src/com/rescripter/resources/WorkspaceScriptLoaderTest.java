package com.rescripter.resources;

import static com.rescripter.test.matchers.PathMatcher.a_path_matching;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.resources.FileContentsReader;
import com.rescripter.resources.WorkspaceScriptLoader;
import com.rescripter.script.ScriptRunner;
import com.rescripter.script.ScriptStack;
import com.rescripter.test.matchers.WorkspaceScriptLoaderMatcher;

public class WorkspaceScriptLoaderTest {
	
	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};

	@Test public void
	loads_a_file_from_the_workspace() throws IOException, CoreException {
		
		final ScriptRunner scriptRunner = context.mock(ScriptRunner.class);
		final ScriptStack scriptStack = context.mock(ScriptStack.class);
		final FileContentsReader fileReader = context.mock(FileContentsReader.class);
		
		final IFile location = context.mock(IFile.class, "location");
		final IContainer container = context.mock(IContainer.class);
		
		final String otherFilename = "other/file.rs";
		final IFile otherFile = context.mock(IFile.class, "otherfile");
		final String otherContents = "some.script();\n";
		final InputStream inputStream = context.mock(InputStream.class);
		
		context.checking(new Expectations() {{
			oneOf(location).getParent(); will(returnValue(container));
			oneOf(container).getFile(with(a_path_matching(otherFilename))); will(returnValue(otherFile));
			oneOf(otherFile).exists(); will(returnValue(true));
			oneOf(otherFile).getContents(); will(returnValue(inputStream));
			oneOf(fileReader).getContents(inputStream); will(returnValue(otherContents));
			oneOf(scriptStack).push(with(WorkspaceScriptLoaderMatcher.a_loader_relative_to(otherFilename)));
			oneOf(scriptRunner).run(otherContents, otherFilename);
			oneOf(scriptStack).pop();
			
			allowing(otherFile).getFullPath(); will(returnValue(new Path(otherFilename)));
		}});
		
		WorkspaceScriptLoader loader = new WorkspaceScriptLoader(location, scriptRunner, scriptStack, fileReader);
		
		loader.file(otherFilename);
		
		context.assertIsSatisfied();
	}
}
