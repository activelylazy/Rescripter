
function runJasmine() {
	var jasmineEnv = jasmine.getEnv();
	jasmineEnv.updateInterval = 1000;
	var trivialReporter = new RescripterReporter();
	jasmineEnv.addReporter(trivialReporter);
	jasmineEnv.execute();
	while(!trivialReporter.finished) {
		TestResult.updateResults();
	    java.lang.Thread.sleep(500);
	}
	TestResult.updateResults();
	
}	

function RescripterReporter() {
    this.messages = [];
	this.failed = false;
	this.finished = false;
	
    this.reportRunnerStarting = function(runner) {
    	TestResult.startTest(runner.specs().length);
    };
        
    this.reportRunnerResults = function(runner) {
    };
    
    this.reportSuiteResults =  function(suite) {
        this.finished = true;
        TestResult.specStarted();	// To finish
    };
    
    this.reportSpecStarting = function(spec) {
    	TestResult.specStarted();
    };
    
    this.collateFailures = function(spec) {
    	var messages = [];
    	foreach(spec.results().getItems(), function(item) {
    		if (item != "Passed.") {
    			messages.push(item); 
    		}
    	});
    	return messages.join(", ");
    }
    
    this.reportSpecResults = function(spec) {
        this.failed = this.failed || spec.results().failedCount > 0
        if (this.failed) {
        	this.messages.push(spec.suite.description+" "+spec.description+": "+this.collateFailures(spec));	
        }
        TestResult.queueResult(spec.suite.description, spec.description, this.collateFailures(spec), ! this.failed);
    };
    
    this.log = function(msg) {
    };
    
    return this;    
}

