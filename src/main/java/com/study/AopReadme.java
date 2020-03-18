package com.study;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class AopReadme {

	void readme() {
		//AopNamespaceHandler AnnotationAwareAspectJAutoProxyCreator
		//Aspect
		//JoinPoint
		//Pointcut
		//Advice
		//Introduction
		//TxNamespaceHandler
		//AOP APIS
		//1.Pointcut
		//1.1 pointcuts操作：交集&并集
		//1.1.1：并集 ，方法被pointcuts中任意一个匹配即可。
		//1.1.2：交集，方法被所有pointcuts匹配。
		//合并或交叉pointcut代表类:org.springframework.aop.support.Pointcuts |ComposablePointcut
		//1.2 AspectJ expression pointcuts
		//1.2.1 org.springframework.aop.aspectj.AspectJExpressionPointcut
		//特点：利用Aspectj提供的类库去解析切点表达式
		//1.3 pointcut的实现
		//ControlFlowPointcut
		//MethodInterceptor
		//MethodBeforeAdvice
		//ThrowsAdvice
		//DefaultPointcutAdvisor
		
		//ProxyFactoryBean
		//DebugInterceptor
		//Advisor
		//Advised
		//AopUtils
		
		//TargetSource
		//HotSwappableTargetSource
		//AbstractPoolingTargetSource
		//PrototypeTargetSource
		//ThreadLocalTargetSource
		
		
		//MessageSource
		//ResourceBundleMessageSource
		//MessageCodesResolver
		//ValidationUtils
		//Errors BindingResult
		//BeanWrapper PropertyEditor
		//BeanWrapperImpl
		//BeanFactoryPostProcessor
//		TransactionInterceptor
		//SocketTimeoutException 
		//SocketException
	}
}
