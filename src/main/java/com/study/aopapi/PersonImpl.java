package com.study.aopapi;

public class PersonImpl implements Person {

	private String name;
	private int age;

	@Override
	public void speak(String worlds) {
		System.out.println(name + " speaks " + worlds);
	}

	@Override
	public void eat(String food) {
		System.out.println(name + " eats " + food);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
