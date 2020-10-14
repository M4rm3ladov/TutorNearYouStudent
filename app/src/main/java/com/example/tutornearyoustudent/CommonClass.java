package com.example.tutornearyoustudent;

import com.example.tutornearyoustudent.Model.StudentInfoModel;

public class CommonClass {
    public static final String STUDENT_INFO_REFERENCE = "StudentInfo";
    public static StudentInfoModel currentUser;

    public static String buildWelcomeMessage() {
        if (CommonClass.currentUser != null){
            return ("Welcome: ")
                    + (CommonClass.currentUser.getFirstName())
                    + (" ")
                    + (CommonClass.currentUser.getLastName().toString());
        }else{
            return "";
        }
    }
}
