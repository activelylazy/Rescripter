package com.rescripter.script;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.rescripter.syntax.ASTTokenFinder;
import com.rescripter.syntax.ChangeText;

public class ScriptRunnerTest {

    public static class CheckCalled {
        boolean called = false;
        
        public void call() {
            called = true;
        }
    }
    
    @Test public void
    runs_script() throws IOException {
        ScriptRunner runner = new ScriptRunner(null);
        CheckCalled stuff = new CheckCalled();
        runner.putProperty("test",stuff);
        runner.run("test.call()","test source",null);

        assertTrue(stuff.called);
    }
    
    @Test public void
    scope_includes_required_classes() throws IOException {
    	ScriptRunner runner = new ScriptRunner(null);
    	assertThat(runner.getProperty("Load"), instanceOf(ScriptLoader.class));
    	assertThat(runner.getProperty("TestResult"), instanceOf(TestResultPublisher.class));
    	assertThat(runner.getProperty("Alert"), instanceOf(Alerter.class));
    	assertThat(runner.getProperty("SearchHelper"), instanceOf(SearchHelper.class));
    	assertThat(runner.getProperty("ChangeText"), instanceOf(ChangeText.class));
    	assertThat(runner.getProperty("ASTTokenFinder"), instanceOf(ASTTokenFinder.class));
    }
}
