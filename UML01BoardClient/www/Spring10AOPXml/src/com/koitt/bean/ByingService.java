package com.koitt.bean;

public class ByingService {
	
	public String sayGoodbye(String name, int age) {
		System.out.println("Good bye " + name + "(" + age + ")");
		return "Return: sayGoodbye()";
	}
}
