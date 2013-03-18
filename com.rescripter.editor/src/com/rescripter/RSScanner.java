package com.rescripter;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class RSScanner extends RuleBasedScanner {

	public RSScanner(ColorManager manager) {
        IToken string =
            new Token(
                new TextAttribute(manager.getColor(IRSColorConstants.STRING)));

		IRule[] rules = new IRule[] {
		        new WhitespaceRule(new RSWhitespaceDetector()),
		        new SingleLineRule("\"", "\"", string, '\\'),
		        new SingleLineRule("\'", "\'", string, '\\'),
		};

		setRules(rules);
	}
}
