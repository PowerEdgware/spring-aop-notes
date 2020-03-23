package com.study.service;

import java.util.Random;

import org.springframework.dao.PessimisticLockingFailureException;

import com.study.annotation.Idempotent;

public class BusinessService {

	Random rnd = new Random();
	
	public BusinessService() {}

	public String doSomething(String params) {
		int range = rnd.nextInt(100);
		System.out.println("range=" + range);
//		if (range > 10) {
//			throw new PessimisticLockingFailureException("acquire lock failed");
//		}
		System.out.println("dosomething with params=" + params+ getClass());
		return "OK";
	}
	
	@Idempotent
	public String doSomethingWithAnno(String params) {
		int range = rnd.nextInt(100);
		System.out.println("range=" + range);
		if (range > 50) {
			throw new PessimisticLockingFailureException("acquire lock failed");
		}
		System.out.println("doSomethingWithAnno with params=" + params);
		return "OK";
	}
}
