package com.example.mymed;

public class User {
    private String name;
    private String surname;
    private String birth_date;
    private String doctor;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", doctor='" + doctor + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getDoctor() {
        return doctor;
    }
    public User(){}
    public User(String name, String surname, String birth_date, String doctor) {
        this.name = name;
        this.surname = surname;
        this.birth_date = birth_date;
        this.doctor = doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}
