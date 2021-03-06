
function runJasmine() {
    var jasmineEnv = jasmine.getEnv();
    jasmineEnv.updateInterval = 1000;
    var trivialReporter = new RescripterReporter();
    TestResult.startTest();
    jasmineEnv.addReporter(trivialReporter);
    jasmineEnv.execute();
    while(!trivialReporter.finished) {
        TestResult.updateResults();
        java.lang.Thread.sleep(500);
    }
    TestResult.updateResults();
    
    if (trivialReporter.failed) {
        Debug.message("Messages are: "+trivialReporter.messages);
        Alert.info("Test failed\n"+trivialReporter.messages.join("\n\n"));
    } else {
        Alert.info("Tests passed");
    }
}   

function RescripterReporter() {
    this.messages = [];
    this.failed = false;
    this.finished = false;
    
    this.reportRunnerStarting = function(runner) {
    };
        
    this.reportRunnerResults = function(runner) {
    };
    
    this.reportSuiteResults =  function(suite) {
        this.finished = true;
    };
    
    this.reportSpecStarting = function(spec) {
    };
    
    this.collateFailures = function(spec) {
        var messages = [];
        foreach(spec.results().getItems(), function(item) {
            if (item != "Passed.") {
                messages.push(item); 
            }
        });
        return messages.join(", ");
    };
    
    this.reportSpecResults = function(spec) {
        this.failed = this.failed || spec.results().failedCount > 0;
        if (this.failed) {
            this.messages.push(spec.suite.description+" "+spec.description+": "+this.collateFailures(spec));    
        }
        TestResult.queueResult(spec.suite.description, spec.description, this.collateFailures(spec));
    };
    
    this.log = function(msg) {
    };
    
    return this;    
}

