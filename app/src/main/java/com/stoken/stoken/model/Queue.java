package com.stoken.stoken.model;

public class Queue {
    public String position;
    public String token;
    public String counter;
    public String student_id;
    public String student_name;

    public Queue() {
    }

    public Queue(String position, String token, String counter, String student_id, String student_name) {
        this.position = position;
        this.token = token;
        this.counter = counter;
        this.student_id = student_id;
        this.student_name = student_name;
    }

    public String getPosition() {
        return position;
    }

    public String getToken() {
        return token;
    }

    public String getCounter() {
        return counter;
    }

    public String getStudentID() {
        return student_id;
    }

    public String getStudentName() {
        return student_name;
    }
}
