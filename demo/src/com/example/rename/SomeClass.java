package com.example.rename;

public class SomeClass {

    public SomeClass() {
        
    }
    
    public String getName() {
        return "Fred";
    }
    
    public static void main(String[] args) {
        SomeClass someClass = new SomeClass();
        
        System.out.println("The name is "+someClass.getName());
    }
}
