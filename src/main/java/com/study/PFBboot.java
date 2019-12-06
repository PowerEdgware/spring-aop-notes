package com.study;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.study.aopapi.Person;
import com.study.aopapi.PersonImpl;
import com.study.aopapi.PersonUser;

/**
 * 测试 ProxyFactoryBean创建Bean
 * 
 * @author shangcj
 *
 */
public class PFBboot {

	public static void main(String[] args) {

		BeanFactory ctx = new ClassPathXmlApplicationContext("aopapi-proxy-factorybean.xml");

		Person person = ctx.getBean(Person.class);
		System.out.println(person.getClass());

		person.speak("hello all");

		PersonUser personUser = ctx.getBean(PersonUser.class);
		personUser.printPerson();

		// render interface
		/**
		 * interface com.study.aopapi.Person  interface
		 * org.springframework.aop.SpringProxy interface
		 * org.springframework.aop.framework.Advised interface
		 * org.springframework.core.DecoratingProxy
		 */
		for (Class cls : person.getClass().getInterfaces()) {
			System.out.println(cls);
		}
	}
}
