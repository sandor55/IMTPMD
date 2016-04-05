package com.imtpmd.sandor.imtpmdws.Models;

import java.io.Serializable;

/**
 * Created by sandor on 2-4-2016.
 */
public class Course implements Serializable {           // WAAROM serializable

    public String name;
    public String ects;
    public String grade;
    public String period;


    public Course(String courseName, String ects, String grade, String period){
        this.name = courseName;
        this.ects = ects;
        this.grade = grade;
        this.period = period;
    }

    // ADD GETTERS

    // ADD SETTERS
}