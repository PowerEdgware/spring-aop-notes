package com.study.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.dao.PessimisticLockingFailureException;

import com.study.annotation.Idempotent;

//@Order 
@Aspect
public class ConcurrentOperationExecutorAspect implements Ordered {

	private int order;// 切面顺序

	private static final int DEFAULT_MAX_RETRIES = 2;

	private int maxRetries = DEFAULT_MAX_RETRIES;

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	// @annotation(com.study.annotation.Idempotent)具有该Idempotent注解的方法
	// @annotation(idempotent)获取annotation，advice方法参数需要有该形参注解
	@Around(value = "com.study.SystemArchitecture.businessService() && @annotation(com.study.annotation.Idempotent)")
	public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
		int attemps = 0;
		PessimisticLockingFailureException lockFailureException;
		do {
			attemps++;
			try {
				return pjp.proceed();
			} catch (PessimisticLockingFailureException ex) {
				ex.printStackTrace();
				lockFailureException = ex;
			}
		} while (attemps <= this.maxRetries);
		throw lockFailureException;
	}

}
