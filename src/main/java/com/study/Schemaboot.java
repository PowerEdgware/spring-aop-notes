package com.study;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.study.service.BusinessService;

public class Schemaboot {

	public static void main(String[] args) {
		 BeanFactory ctx = new ClassPathXmlApplicationContext("schema-based-aop.xml");
		 
		 BusinessService businessService=ctx.getBean(BusinessService.class);
//		String result= businessService.doSomething("hello");
		String result= businessService.doSomethingWithAnno("hello");
		
		System.out.println(result);
		
		
	}
}
