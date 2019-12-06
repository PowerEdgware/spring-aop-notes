package com.study.aopapi;

public class PersonUser {

	private Person person;

	public void printPerson() {
		System.out.println("hold proxied person instance=" + person.getClass());
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
