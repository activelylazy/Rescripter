package com.example;

public class Person {

	private String name;
	private int age;
	
    public Person() { }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public void setAge(String age) { this.age = Integer.parseInt(age); }
    
    public static void main(String[] args) {
        Person person = new Person();
        
        System.out.println("The name is "+person.getName());
    }
}
