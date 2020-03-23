package com.study;

import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.study.service.BusinessService;

public class LoadTimeWeaverAspectBoot {

	public static void main(String[] args) {
		AbstractRefreshableConfigApplicationContext context=new ClassPathXmlApplicationContext("aop-aspectj-via-loadtimeweaver.xml");
		
		BusinessService businessService=(BusinessService) context.getBean("demoBusiness");
//		System.out.println(businessService.getClass());//class com.study.service.BusinessService
		String resp=businessService.doSomething("hello");
		System.out.println("resp="+resp);
		
		context.close();
	}
}
