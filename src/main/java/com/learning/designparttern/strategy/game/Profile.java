package com.learning.designparttern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:50
 */
public class Profile {
    private String name;
    private String sex;
    private String character;

    public Profile(String name, String sex, String character) {
        this.name = name;
        this.sex = sex;
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", character='" + character + '\'' +
                '}';
    }

    public void showProfile(){
        System.out.println("英雄资料："+this.toString());
    }
}
