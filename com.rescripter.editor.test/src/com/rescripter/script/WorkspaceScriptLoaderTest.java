package com.rescripter.script;

import static com.rescripter.test.matchers.PathMatcher.a_path_matching;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.test.matchers.WorkspaceScriptLoaderMatcher;

public class WorkspaceScriptLoaderTest {
	
	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};

	@Test public void
	loads_a_file_from_the_workspace() throws IOException, CoreException {
		
		final ScriptRunner scriptRunner = context.mock(ScriptRunner.class);
		final ScriptStack scriptStack = context.mock(ScriptStack.class);
		final WorkspaceFileReader fileReader = context.mock(WorkspaceFileReader.class);
		
		final IFile location = context.mock(IFile.class, "location");
		final IContainer container = context.mock(IContainer.class);
		
		final String otherFilename = "other/file.rs";
		final IFile otherFile = context.mock(IFile.class, "otherfile");
		final String otherContents = "some.script();\n";
		
		context.checking(new Expectations() {{
			oneOf(location).getParent(); will(returnValue(container));
			oneOf(container).getFile(with(a_path_matching(otherFilename))); will(returnValue(otherFile));
			oneOf(otherFile).exists(); will(returnValue(true));
			oneOf(fileReader).getContents(otherFile); will(returnValue(otherContents));
			oneOf(scriptStack).push(with(WorkspaceScriptLoaderMatcher.a_loader_relative_to(otherFilename)));
			oneOf(scriptRunner).run(otherContents, otherFilename, otherFile);
			oneOf(scriptStack).pop();
			
			allowing(otherFile).getFullPath(); will(returnValue(new Path(otherFilename)));
		}});
		
		WorkspaceScriptLoader loader = new WorkspaceScriptLoader(location, scriptRunner, scriptStack, fileReader);
		
		loader.file(otherFilename);
		
		context.assertIsSatisfied();
	}
}
