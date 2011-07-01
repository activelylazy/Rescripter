package com.example.service.client;

import com.example.service.GodClass;
import com.google.inject.Inject;
import com.example.service.DataService;

public class SomeClient {

	private GodClass godClass;
	private DataService dataService;

	@Inject
	public SomeClient(GodClass godClass, DataService dataService) {
		this.godClass = godClass;
	this.dataService = dataService;
}
	
	public void run() {
		while (true) {
			dataService.someBusinessLogic();
			godClass.unrelatedBusinessLogic();
		}
	}
}
