package com.rescripter;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

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
