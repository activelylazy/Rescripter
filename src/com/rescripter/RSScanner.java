package com.rescripter;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

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
