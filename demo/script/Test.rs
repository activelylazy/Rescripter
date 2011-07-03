
var Finder = {
	typeByName: function(name) {
		return Find.typeByName(name);
	},
	
	methodByName: function(type, methodName) {
		var methods = type.getMethods();
		for (var i=0; i<methods.length; i++) {
			if (methods[i].getElementName() == methodName) {
				return methods[i];
			}
		}
	},
	
	methodsByName: function(type, methodName) {
		var methods = type.getMethods();
		var matching = [];
		for (var i=0; i<methods.length; i++) {
			if (methods[i].getElementName() == methodName) {
				matching.push(methods[i]);
			}
		}
		return matching;
	},
	
	referencesTo: function(element) {
		return Find.referencesTo(element);
	},
	
	tokensWithin: function(match,  fun) {
		ASTTokenFinder.scanTokens(match.getElement().getCompilationUnit(), match.getOffset(), match.getLength(), fun);
	},
	
	identifiersWithin: function(match, fun) {
		Finder.tokensWithin(match, function(tokenType, offset, length) {
			if(tokenType = org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameIdentifier) {
				fun(tokenType, offset, length);
			}
		});
	}
};

var Rename = {
	method: function(method, newName) {
		return new org.eclipse.text.edits.ReplaceEdit(method.getNameRange().getOffset(), method.getNameRange().getLength(), newName)
	}
};

function DocumentEdit(cu) {
	this.cu = cu;
	this.textEdit = new org.eclipse.text.edits.MultiTextEdit();
	
	this.apply = function() {
		this.cu.becomeWorkingCopy(null);
		this.cu.applyTextEdit(this.textEdit, null);
		this.cu.commitWorkingCopy(true, null);
	};
	
	this.append = function(textedit) {
		this.textEdit.addChild(textedit);
	}
	
	this.insert = function(offset, text) {
		this.append(new org.eclipse.text.edits.InsertEdit(offset, text));
	}
	
	this.replace = function(offset, length, text) {
		this.append(new org.eclipse.text.edits.ReplaceEdit(offset, length, text));
	}
	
	return this;
}

function transform(list, fun) {
	for(var i=0; i<list.length; i++) {
		fun(list[i]);
	}
}



var person = Finder.typeByName("Person");
var method = Finder.methodByName(person, "getName");

transform(Finder.referencesTo(method), function(reference) {
	Finder.identifiersWithin(reference, function(tokenType, offset, length) {
		Alert.info("Got an identifier: "+tokenType+" at "+offset+" length "+length);
	});
});


var edit = new DocumentEdit(person.getCompilationUnit())
edit.insert(0, "hello world");
edit.append(Rename.method(method, "yoMamma"));
edit.apply();

Alert.info("Done");