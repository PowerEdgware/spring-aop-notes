package com.study.staticproxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Aspect
public class ProfilingAspect {


    @Around("methodsToBeProfiled()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
    	System.out.println("before!");
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start(pjp.getSignature().getName());
            Object ret= pjp.proceed();
            System.out.println("after! "+ret);
            return ret;
        } finally {
            sw.stop();
            System.out.println(sw.prettyPrint());
        }
    }
    //com.study..service.*.*(..)
    //execution(* com.study..service.*.*(..))
    @Pointcut("execution(* com.study.service.*.*(..))")
    public void methodsToBeProfiled(){}
}
