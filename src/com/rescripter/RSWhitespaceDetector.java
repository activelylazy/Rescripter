package com.rescripter;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class RSWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
