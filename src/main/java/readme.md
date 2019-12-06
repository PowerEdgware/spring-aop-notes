### Aspect Oriented Programming with Spring###  

###面向切面编程  
> spring 提供了 schema-based AOP 和@AspectJ方式 AOP支持。 两种方式都用了AspectJ的切点表达式语言。  
> [AOP中一些术语](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#aop-introduction-defn)

####aop在springframework中的应用
** 1.声明式服务 ，典型应用：[声明式事务](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/data-access.html#transaction-declarative)**  
** 2.呈现应用级别的OOP代理. **  

#### @AspectJ 方式实现AOP

#### spring中激活 @AspectJ支持

> 引入aspectjweaver.jar依赖。使用@Aspect定义切面aspect  

* Java Configuration激活方式  

```
	@Configuration
	@EnableAspectJAutoProxy
	public class AppConfig {
	
	}
```

* XML Configuration激活方式  
`<aop:aspectj-autoproxy/>`  

----

   + 声明切面  

```
import org.aspectj.lang.annotation.Aspect;
@Aspect
public class NotVeryUsefulAspect {

}
```
以`@Aspect`注解的pojo就是一个切面，切面可以包含方法和属性，也可以包含 pointcut, advice等。  
切面需要交给spring容器管理以便spring发现和使用该切面。    

   + 定义切点 
   
```
   	@Pointcut("execution(* transfer(..))")// the pointcut expression
	private void anyOldTransfer() {}// the pointcut signature
```

`@Pointcut`属性：  
execution： 匹配方法执行;  
this: 匹配当前代理对象为指定类型的实例，如：this(com.xyz.service.AccountService) 代表匹配代理类实现AccountService 接口的类型;   
target:目标对象;  
within:such as within the service package:within(com.xyz.service.*)  
args:方法参数;   
@target:匹配目标类指定的注解类型;  
@args:匹配执行实参参数上具有的注解类型;    
@annotation：匹配连接点(执行的目标方法)具有指定注解。  

切点表达式：

```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
            throws-pattern?)
```
   + 定义通知    
   定义形式： 可以是引用带有名称的切点，也可以单独定义切点表达式。[参考](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#aop-advice)  
   
   ```
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class BeforeExample {

    @Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")//引用带名称的切点
    public void doAccessCheck() {
        // ...
    }
    
    @Before("execution(* com.xyz.myapp.dao.*.*(..))")//自定义切点表达式
    public void doCheck() {
        // ...
    }
}
```
其中带名称的切点定义为：  

```
package com.xyz.someapp;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SystemArchitecture  {
	@Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
	    public void dataAccessOperation() {}
	}
}
```

* [通知执行的顺序](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#aop-ataspectj-advice-ordering)  
总结如下：  
**a.** 多个通知在同一个连接点(目标方法调用处)运行，进入方法时，优先级高的先执行；出方法时，优先级高的后执行。  
**b.** 两个定义在不同切面(aspect)的通知在同一个连接点(目标方法调用处)运行，不指定顺序，执行顺序不确定。指定顺序的方式：  
切面实现`org.springframework.core.Ordered`接口或者指定`org.springframework.core.annotation.Order`注解.  
不管哪种方式，切面中`Ordered.getValue()`的值越小，具有越高的优先级。  
**c.** 两个定义在同一个切面的通知，在同一个连接点(目标方法调用处)运行,执行是无顺序的(没法确定顺序)，建议拆成一个通知或者分配到不同的切面中去。  



一个例子：参考`com.study.aspectj.*`

----

#### Schema-based 方式实现AOP  
#### 激活spring-aop `<aop:config>`配置文件中定义  
> 基于XML文件 使用aop命名空间来定义切面aspect。所有的aspect and advisor元素必须放在`<aop:config>`元素中。  
> 此`<aop:config>`元素标签可以包含 pointcut, advisor, and aspect 等元素标签。

+ 定义切面

> 定义的切面可以是一个java对象，并当作spring容器的Bean进行管理  

```
<aop:config>
    <aop:aspect id="myAspect" ref="aBean">
        ...
    </aop:aspect>
</aop:config>

<bean id="aBean" class="...">
    ...
</bean>
```

+ 定义切点  

> 切点使用 AspectJ 切点表达式语言，可以定义全局切点由其他切面共享，也可以在某个切面内部单独定义。

1.共享切点  

```
<aop:config>
    <aop:pointcut id="businessService"
        expression="execution(* com.xyz.myapp.service.*.*(..))"/>//表达式可以自定义也可以引用@Aspect定义中的。
</aop:config>
```
2.切面内部定义的私有切点  

```
<aop:config>
    <aop:aspect id="myAspect" ref="aBean">

        <aop:pointcut id="businessService"
            expression="execution(* com.xyz.myapp.service.*.*(..))"/>
        ...
    </aop:aspect>
</aop:config>
```

+ 定义通知  

>  schema-based AOP 五种通知类型和@AspectJ 方式的类似 。他被定义在`<aop:aspect>`标签内部。

```
<aop:aspect id="beforeExample" ref="aBean">

    <aop:before
        pointcut-ref="dataAccessOperation"
        method="doAccessCheck"/>
    ...
</aop:aspect>
```

**注意**   
After Returning Advice通知是在目标方法正常完成返回后执行。  
After (Finally) Advice 通知不管目标方法以什么方式执行完，他都会执行。    
Around Advice通知  环绕目标执行方法执行，比如before after.也可以不执行目标方法。  

+ 定义通知参数  

> 参考 @Aspect中 [Advice Parameters](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#aop-ataspectj-advice-params)

```
<aop:before
    pointcut="com.xyz.lib.Pointcuts.anyPublicMethod() and @annotation(auditable)"
    method="audit"
    arg-names="auditable"/>
```


+ Advisors  

>Advisor的概念来源于spring,和@Aspectj没什么直接联系 。  
>他就像一个切面aspect，也包含一系列的advice,advice是一个实现了`advice interfaces`的bean. advisor也可以使用@Aspectj切点表达式。  
>支持的标签 `<aop:advisor>`  通常用于spring事务  
>可以通过advice的`order `属性给advisor定义切面执行顺序。  

定义：

```
<aop:config>

    <aop:pointcut id="businessService"
        expression="execution(* com.xyz.myapp.service.*.*(..))"/>

    <aop:advisor
        pointcut-ref="businessService"
        advice-ref="tx-advice"/>

</aop:config>

<tx:advice id="tx-advice">
    <tx:attributes>
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>
```

** Schema-based AOP ** 环绕通知的例子`com.study.schemaaop.*`   

----

#### Spring AOP APIs 介绍

+ Pointcut API  
接口：`org.springframework.aop.Pointcut`  

```
public interface Pointcut {
ClassFilter getClassFilter();
MethodMatcher getMethodMatcher();
}
```

###  Spring AOP APIs  
>介绍spring-aop中一些API接口

#### Pointcut API

+ pointcut接口
`org.springframework.aop.Pointcut`  
定义： 
 
```
public interface Pointcut {
ClassFilter getClassFilter();//目标类匹配
MethodMatcher getMethodMatcher();//目标方法匹配
}

public interface ClassFilter {
boolean matches(Class clazz);
}

public interface MethodMatcher {
boolean matches(Method m, Class targetClass);//匹配目标类中的方法，匹配时机：AOP proxy is created，属于静态匹配调用
boolean isRuntime();//MethodMatcher是静态的，则返回false
boolean matches(Method m, Class targetClass, Object[] args);//上述两个方法都返回true,此方法会在每次方法通知执行之前动态调用，判断目标通知是否被执行。
}
```

+ pointcus操作 ：交集&并集  
并集 ，方法被pointcuts中任意一个匹配即可。  
交集，方法被所有pointcuts匹配。  
以组合的形式生成 Pointcut:  
`org.springframework.aop.support.Pointcuts`  
`org.springframework.aop.support.ComposablePointcut`  

+ AspectJ expression pointcuts解析Aspectj表达式的poingcut

> 特点：利用Aspectj提供的类库去解析切点表达式

代表类：
`org.springframework.aop.aspectj.AspectJExpressionPointcut`  
 
+ spring pointcut的常规实现  
 ** Static pointcuts静态方式**   
 基于目标类和方法，不考虑方法参数。只匹配一次，后续的方法调用中不再匹配。  匹配结果可缓存  
 
 典型代表：
 Regular expression pointcuts  正则匹配方式  
 类：`org.springframework.aop.support.JdkRegexpMethodPointcut`
 
 ** Dynamic pointcuts 动态方式 **  
 除了静态部分，它还匹配方法参数，也就是每次方法调用之前都会根据方法参数进行匹配处理，以决定是否调用通知方法。   
 因方法参数是变化的，因此不能缓存匹配结果。  
 
 典型代表：
 Control flow pointcuts
 类： `org.springframework.aop.support.ControlFlowPointcut`   
 比如匹配某个连接点方法是否被某个包下方法或者类调用，从而实现动态匹配。  
 
 + Pointcut superclasses 实现自定义pointcut
 类：`StaticMethodMatcherPointcut`  
 
 ```
 class TestStaticPointcut extends StaticMethodMatcherPointcut {
public boolean matches(Method m, Class targetClass) {
// return true if custom criteria match
}
}
```

#### Advice API  

>spring 如何处理这些advice

+ Advice lifecycles生命周期   
每个advice都是spring的bean   

+ Advice types类型  

**Interception around advice **  
Interceptor类型的环绕通知  ：`org.aopalliance.intercept.MethodInterceptor`  
不同于cglib中的`MethodInterceptor`

```
public interface MethodInterceptor extends Interceptor {
	Object invoke(MethodInvocation invocation) throws Throwable;//类似调用 @link Joinpoint#proceed()
}
```
上述方法调用参数中有：目标方法(连接点)，aop proxy,方法参数。

```
public class DebugInterceptor implements MethodInterceptor {
	public Object invoke(MethodInvocation invocation) throws Throwable {
	System.out.println("Before: invocation=[" + invocation + "]");
	Object rval = invocation.proceed();//使用拦截器链 朝目标方法调用
	System.out.println("Invocation returned");
	return rval;
}
}
```

**Before advice ** 
>目标方法执行之前调用的Advice，不需要，MethodInvocation。

`org.springframework.aop.MethodBeforeAdvice`

```
public interface MethodBeforeAdvice extends BeforeAdvice {
	void before(Method m, Object[] args, Object target) throws Throwable;//没有返回值，也不行改变目标方法的执行结果
}
```

** Throws advice**  
>目标方法抛出异常时执行的通知。`org.springframework.aop.ThrowsAdvice` a tag interface

方法存在形式：
`afterThrowing([Method, args, target], subclassOfThrowable)`  

前面三个参数可选，subclassOfThrowable必选。  

>Tips: 异常通知中可以抛出RuntimeException 也可以抛出checked exception 但是必须和目标方法匹配相同的方法签名。  
>不要抛出和目标方法不兼容的且未声明的检查时异常(checked exception)

例子：  

```
public class ServletThrowsAdviceWithArguments implements ThrowsAdvice {
	public void afterThrowing(Method m, Object[] args, Object target, ServletException ex) {
	// Do something with all arguments
	}
}
```
上述通知 在目标方法抛出`ServletException` 时会被调用，并且把额外的参数暴露出来，此方法可以直接访问这些参数。  

** After Returning advice**  

```
public interface AfterReturningAdvice extends Advice {
	void afterReturning(Object returnValue, Method m, Object[] args, Object target)
	throws Throwable;
}
```
返回值returnValue不能修改，它也不能改变执行路径，如果它抛出了异常，那么整个拦截执行链路会被打破。  

+ Advisor API  

> spring中Advisor代表一个切面aspect,它只包含一个与切入点表达式相关联的通知对象。它可以用于任何类型的advice通知。
>比如：MethodInterceptor, BeforeAdvice or ThrowsAdvice等。


spring常用的Advisor:
`org.springframework.aop.support.DefaultPointcutAdvisor`  


+ Using the ProxyFactoryBean to create AOP proxies

>使用ProxyFactoryBean创建代理 ，基于FactoryBean.advice and pointcut都是交给springIOC管理

javaBean ProxyFactoryBean 主要的属性：  
**JavaBean properties**   
1，指定被代理对象  
2，指定是否使用CGLIB代理。  


父类`org.springframework.aop.framework.ProxyConfig`中的一些属性：  
a.proxyTargetClass true使用CGLIB代理；  
b.optimize 使用cglib创建代理时的激进优化；   
c.frozen 标记proxy配置是否被冻结，默认false，一旦为true,则不允许修改配置，也不能通过强转proxy为`Advised`从而动态控制代理对象。比如向代理中动态添加通知等。  
d.exposeProxy 暴露代理对象，以便在目标方法中获取代理。  

`ProxyFactoryBean`自身具备的属性：  
a.proxyInterfaces  需要代理的接口  
b.interceptorNames 通知的名字（String array of Advisor, interceptor or other advice names to apply. ），顺序是先来先服务的规则，可使用通配符。  

 ** JDK- and CGLIB-based proxies(jdk or cglib代理)  **  
 
 使用jdk还是cglib实现代理创建?  
 a.如果目标类没有接口，则使用cglib代理  
 b.如果目标类实现一个或多个接口，使用哪种代理取决于`ProxyFactoryBean.proxyTargetClass`属性配置。如果设置为：true  
 那么使用cglib代理，即使属性`proxyInterfaces`设置了多个接口，只要`proxyTargetClass=true`仍然使用cglib代理。  
 c.proxyInterfaces设置了接口，`proxyTargetClass=false`，那么会使用jdk代理方式，如果目标类实现的接口多于`proxyInterfaces`  
 配置的，那么jdk方式创建的代理，只实现了配置的接口，而目标类多出的接口则不被代理。最好的方式就是不设置`proxyInterfaces`属性，`ProxyFactoryBean`会自动发现目标类的的所有接口并创建jdk代理。  
 
  
 ** 使用 ProxyFactoryBean创建代理的一个例子**  
 `com.study.aopapi.*`  启动类 `com.study.PFBboot`  
 
 
+ Manipulating advised objects 操作被代理对象  

>所有Spring AOP代理对象都可以被强转成`org.springframework.aop.framework.Advised`接口，该接口保存了AOP工厂代理的所有配置，包括 'Interceptors and other advice, Advisors, and the proxied interfaces'等。可以用Advised对象去 动态CRUD 它内部的属性或通知。
> 如果设置Advised.isFrozen() flag=true,那么该配置不能在被修改。任何的修改删除都会报错`AopConfigException`



