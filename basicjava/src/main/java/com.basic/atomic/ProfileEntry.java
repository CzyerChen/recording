package com.basic.atomic;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 28 16:16
 */
public class ProfileEntry {
    private  String id;
    private String name;
    public volatile int age;

    public ProfileEntry(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    @Override
    public String toString() {
        return "ProfileEntry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
