package com.rescripter.script;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScriptRunnerTest {

    public static class CheckCalled {
        boolean called = false;
        
        public void call() {
            called = true;
        }
    }
    
    @Test public void
    runs_script() {
        ScriptRunner runner = new ScriptRunner();
        CheckCalled stuff = new CheckCalled();
        runner.putProperty("test",stuff);
        runner.run("test.call()","test source");

        assertTrue(stuff.called);
    }
}
