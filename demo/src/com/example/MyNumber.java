package com.example;
import static com.example.MyNumber.valueOf;

public class MyNumber {

	private int value;
	
	public MyNumber(String value) {
		this.value = Integer.parseInt(value);
	}
	
	public static MyNumber valueOf(String value) {
		return valueOf(value);
	}
}
