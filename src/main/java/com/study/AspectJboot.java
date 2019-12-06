package com.study;

import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.study.service.BusinessService;

public class AspectJboot {

	public static void main(String[] args) {
		
		
		AbstractRefreshableApplicationContext context=new ClassPathXmlApplicationContext("classpath:aop-aspectj.xml");
		context.refresh();
		
		BusinessService businessService=context.getBean(BusinessService.class);
		
		String resp=businessService.doSomething("hello");
//		String resp=businessService.doSomethingWithAnno("hello");
		System.out.println("resp="+resp);
		
		context.close();
	}
}
