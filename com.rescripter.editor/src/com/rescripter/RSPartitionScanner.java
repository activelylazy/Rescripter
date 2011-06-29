package com.rescripter;

import org.eclipse.jface.text.rules.*;

public class RSPartitionScanner extends RuleBasedPartitionScanner {
	public final static String COMMENT = "__comment";

	public RSPartitionScanner() {

		IToken vsComment = new Token(COMMENT);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new EndOfLineRule("//", vsComment); 
		rules[1] = new MultiLineRule("/*", "*/", vsComment);

		setPredicateRules(rules);
	}
}
