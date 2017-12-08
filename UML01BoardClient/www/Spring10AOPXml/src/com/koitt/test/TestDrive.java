package com.koitt.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.koitt.bean.ByingService;
import com.koitt.bean.GreetingService;

public class TestDrive {
	
	private static final String path = 
			"/com/koitt/config/config.xml";
	
	public static void main(String[] args) {
		ApplicationContext context = 
				new GenericXmlApplicationContext(path);
		
		GreetingService service = context.getBean("greeting", GreetingService.class);
		service.sayHello();
		
		ByingService service2 = context.getBean("bying", ByingService.class);
		String result = service2.sayGoodbye("Jeong", 32);
		System.out.println(result);
	}
}
