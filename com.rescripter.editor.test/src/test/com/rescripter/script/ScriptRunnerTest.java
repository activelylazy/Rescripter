package com.rescripter.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.rescripter.resources.FileContentsReader;
import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;

public class ScriptRunnerTest {
	
	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	
    public static class CheckCalled {
        boolean called = false;
        
        public void call() {
            called = true;
        }
    }
    
    @Test public void
    runs_script() throws IOException, CoreException {
    	final FileContentsReader fileReader = context.mock(FileContentsReader.class);
    	
    	context.checking(new Expectations() {{
    		oneOf(fileReader).getContents(with(any(InputStream.class))); will(returnValue(""));
    	}});
    	
        ScriptRunner runner = new ScriptRunner(null, new ScriptStack(), fileReader);
        
        CheckCalled stuff = new CheckCalled();
        runner.putProperty("test",stuff);
        runner.run("test.call()","test source");

        assertThat(stuff.called, is(true));
    }
    
    @Test public void
    scope_includes_required_classes() throws IOException, CoreException {
    	final FileContentsReader fileReader = context.mock(FileContentsReader.class);
    	
    	context.checking(new Expectations() {{
    		oneOf(fileReader).getContents(with(any(InputStream.class))); will(returnValue(""));
    	}});

    	ScriptRunner runner = new ScriptRunner(null, new ScriptStack(), fileReader);
    	assertThat(runner.getProperty("Load"), instanceOf(ScriptStack.class));
    	assertThat(runner.getProperty("TestResult"), instanceOf(TestResultPublisher.class));
    	assertThat(runner.getProperty("Alert"), instanceOf(Alerter.class));
    	assertThat(runner.getProperty("SearchHelper"), instanceOf(SearchHelper.class));
    	assertThat(runner.getProperty("ChangeText"), instanceOf(ChangeText.class));
    	assertThat(runner.getProperty("ASTTokenFinder"), instanceOf(ASTTokenFinder.class));
    }
}
