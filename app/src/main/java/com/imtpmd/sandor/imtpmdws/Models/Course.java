package com.imtpmd.sandor.imtpmdws.Models;

import java.io.Serializable;

/**
 * Created by sandor on 2-4-2016.
 */
public class Course implements Serializable {           // WAAROM serializable

    public String name;
    public int ects;
    public double grade;
    public int period;


                  public Course(String courseName, int ects, double grade, int period){
        this.name = courseName;
        this.ects = ects;
        this.grade = grade;
        this.period = period;
    }

    // ADD GETTERS

    // ADD SETTERS
}