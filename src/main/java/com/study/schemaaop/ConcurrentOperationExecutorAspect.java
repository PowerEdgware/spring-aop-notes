package com.study.schemaaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;
import org.springframework.dao.PessimisticLockingFailureException;


//@Order 
//@Aspect //TODO REMOVED
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
