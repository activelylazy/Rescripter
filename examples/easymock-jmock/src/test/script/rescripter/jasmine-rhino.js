/*
 * Provide missing globals for jasmine when running under rhino
 * Include just this, which in turn includes jasmine.
 */
var window;
(function (global) {
	window = global;
    var timer = new java.util.Timer(); 
    var counter = 1; 
    var ids = {}; 

    global.setTimeout = function (fn, delay) { 
        var id = counter++; 
        ids[id] = new JavaAdapter(java.util.TimerTask, { run: fn }); 
        timer.schedule(ids[id], delay); 
        return id; 
    }; 

    global.clearTimeout = function (id) { 
        ids[id].cancel(); 
        timer.purge(); 
        delete ids[id]; 
    }; 

    global.setInterval = function (fn, delay) { 
        var id = counter++; 
        ids[id] = new JavaAdapter(java.util.TimerTask, { run: fn }); 
        timer.schedule(ids[id], delay, delay); 
        return id; 
    }; 

    global.clearInterval = global.clearTimeout;
    
    global.exports = {};

})(this); 

