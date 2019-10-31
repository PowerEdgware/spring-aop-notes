package com.study;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {

	@Pointcut("execution(* com.study..service.*.*(..))")
	public void businessService() {
	}
}
