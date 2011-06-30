package com.example.rename;

public class Person {

    public Person() {
        
    }
    
    public String getName() {
        return "Fred";
    }
    
    public static void main(String[] args) {
        Person person = new Person();
        
        System.out.println("The name is "+person.getName());
    }
}
