### 基于AspectJ实现的静态代理(Load-time Weaving with AspectJ in the Spring Framework)

#### 基于XML方式配置步骤

基础依赖： 

```
		<spring-version>5.2.4.RELEASE</spring-version>
		<aspectj-version>1.9.4</aspectj-version>
		
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context-support</artifactId>
			<version>${spring-version}</version>
		</dependency>

		<!-- aspectj -->
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjweaver</artifactId>
			<version>${aspectj-version}</version>
		</dependency>
```


1.定义切面

>和由spring AOP创建代理时切面配置没差别。  


```
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
    //execution(* com.study..service.*.*(..))  Aspectj切点表达式
    @Pointcut("execution(* com.study.service.*.*(..))")
    public void methodsToBeProfiled(){}
}
```

2.在类路径下定义`META-INF/aop.xml`文件    

```
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "https://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>
    <weaver options="-verbose -debug -showWeaveInfo">
        <!-- only weave classes in our application-specific packages -->
        <include within="com.study.service..*" />
        <include within="com.study.staticproxy.*" />
        
        <dump within="com.study.service..*"/> 
    </weaver>

    <aspects>
        <!-- weave in just this aspect -->
        <aspect name="com.study.staticproxy.ProfilingAspect"/>
    </aspects>
</aspectj>

```

> 注意：上述定义的切面不需要交给spring容器管理，完全由AspectJ使用，用来告知Aspectj把 ProfilingAspect切面织入我们的classes  
> 同时需要在META-INF/aop.xml中指明哪些classes需要交给Aspect进行织入    
> aop.xml的配置参考[Aspect官网](https://www.eclipse.org/aspectj/doc/released/devguide/ltw-configuration.html)

 遇到的陷阱  ~~  

本次例子中我的service和ProfilingAspect切面分别放置在不同的包中，而我的aop.xml `<weaver>/weaver>`标签配置中并没有把

ProfilingAspect所在的包配置进去，所以一直卡壳，报了下面的错误：  

```
Exception in thread "main" java.lang.NoSuchMethodError: com.study.staticproxy.ProfilingAspect.aspectOf()Lcom/study/staticproxy/ProfilingAspect;
	at com.study.service.BusinessService.doSomething(BusinessService.java:16)
	at com.study.LoadTimeWeaverAspectBoot.main(LoadTimeWeaverAspectBoot.java:18)
```
从错误的类型看，应该是切面类`ProfilingAspect`少了`aspectOf()`方法，而切面配置时并没有这个方法，所以这个方法应该是Aspect帮助织入的，所以后来把切面类
也交给Aspect进行织入。  


折腾了一天报错没有解决，后来我突然想起把切面也加入织入的元素标签内，加入后如下：  

```
    <weaver options="-verbose -debug -showWeaveInfo">
        <!-- only weave classes in our application-specific packages -->
        <include within="com.study.service..*" />
        <include within="com.study.staticproxy.*" /> //之前aop.xml配置文件没有这个配置
        
        <dump within="com.study.service..*"/> 
    </weaver>
```
问题得到解决，切面逻辑正式生效。


3.spring XML配置  
>该配置打开 spring的类加载时织入功能

classpath: `aop-aspectj-via-loadtimeweaver.xml`  

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

<!-- 	<bean class="com.study.staticproxy.ProfilingAspect"/> -->

	<!-- a service object; we will be profiling its methods -->
	<!-- bean definitions here -->
	<bean id="demoBusiness" class="com.study.service.BusinessService" />

    <!-- this switches on the load-time weaving -->
	<!-- 开启loadtime weaver -->
    <context:load-time-weaver aspectj-weaving="on" />
</beans>
```

4.编写的主函数  

```
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
```

5.配置启动参数

Eclipse 里运行时VM参数指定 javaagent： 

`-javaagent:E:\\Maven\\spring-instrument-5.2.4.RELEASE.jar` 

即：`-javaagent:${path to spring-instrument-{version}.jar}` 


运行结果：		

```
before!
[AppClassLoader@18b4aac2] debug not weaving 'org.springframework.util.StopWatch'
range=34
dosomething with params=helloclass com.study.service.BusinessService
after! OK
[AppClassLoader@18b4aac2] debug not weaving 'org.springframework.util.StopWatch$TaskInfo'
StopWatch 'ProfilingAspect': running time = 225000 ns
---------------------------------------------
ns         %     Task name
---------------------------------------------
000225000  100%  doSomething

resp=OK

```

Java agent是 java程序启动时参数

`The -javaagent is a flag for specifying and enabling [agents to instrument programs that run on the JVM](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html). `  

参考利用`java.lang.instrument`实现虚拟机启动(初始化)时，加入一些工具实现加载类之前修改类或方法字节码的方法。  
[一个例子](https://github.com/PowerEdgware/instrument-demo.git)




#### 一些细节 

1.`'META-INF/aop.xml'` [配置](https://www.eclipse.org/aspectj/doc/released/devguide/ltw-configuration.html)  

2.依赖 Required libraries (JARS)  

```
At minimum, you need the following libraries to use the Spring Framework’s support for AspectJ LTW:
spring-aop.jar
aspectjweaver.jar
If you use the Spring-provided agent to enable instrumentation, you also need:
spring-instrument.jar
```

3.Spring Configuration 方式  上面实现为：XML-based configuration方式   

```
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {
}

```

`AspectJWeavingEnabler` `DefaultContextLoadTimeWeaver`  
`InstrumentationLoadTimeWeaver`:JVM started with Spring InstrumentationSavingAgent (java -javaagent:path/to/spring-instrument.jar)  


4.Environment-specific Configuration 容器中使用Spring’s LTW support  

Tomcat, JBoss, WebSphere, WebLogic  

上述容器提供了通用的类加载器实现instrumentation的功能，spring可以借助借助这些类加载器的实现提供 AspectJ weaving(织入)  
可以按照上述例子的配置方式实现load-time weaving，而不用在JVM启动时添加：`-javaagent:path/to/spring-instrument.jar`脚本。  





