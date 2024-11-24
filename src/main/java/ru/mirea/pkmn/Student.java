package ru.mirea.pkmn;

import java.io.Serializable;

public class Student implements Serializable {
    public static final long serialVersionUID = 1L;

    private String firstName;
    private String surName;
    private String familyName;
    private String group;

    public Student(String surName, String firstName, String familyName, String group) {
        this.familyName = familyName;
        this.firstName = firstName;
        this.group = group;
        this.surName = surName;
    }

    public Student() {
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getSurName() {
        return surName;
    }
    public void setSurName(String name) {
        this.surName = name;
    }

    public String getFamilyName() {
        return familyName;
    }
    public void setFamilyName(String name) {
        this.familyName = name;
    }

    public String getGroup() {
        return group;
    }
    public void setGroup(String name) {
        this.group = name;
    }

    @Override
    public String toString() {
        return  "\n" + surName + " " + firstName + " " + familyName + ",\n" + group;
    }
}
