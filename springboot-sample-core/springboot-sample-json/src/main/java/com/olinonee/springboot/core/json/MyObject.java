package com.olinonee.springboot.core.json;

/**
 * MyObject
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
public class MyObject {
    private String name;
    private int age;

    public MyObject(String name, int age) {
        this.name = name;
        this.age = age;
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
