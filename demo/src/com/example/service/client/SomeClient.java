package com.example.service.client;

import com.example.MyNumber;
import com.example.service.GodClass;
import com.google.inject.Inject;

public class SomeClient {

	private GodClass godClass;

	@Inject
	public SomeClient(GodClass godClass) {
		this.godClass = godClass;
	}
	
	public void run() {
		MyNumber myNumber = new MyNumber("42");
		while (true) {
			godClass.someBusinessLogic();
			godClass.unrelatedBusinessLogic();
		}
	}
}
