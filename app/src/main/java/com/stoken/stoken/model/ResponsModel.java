package com.stoken.stoken.model;

import java.util.List;

public class ResponsModel {
    Boolean success;
    String message, user_id, student_id, student_name;

    // data model list
    List<DataModel> results;

    public List<DataModel> getResults() {
        return results;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // user data
    public String getUserID() {
        return user_id;
    }

    public String getStudentID() {
        return student_id;
    }

    public String getName() {
        return student_name;
    }
}
