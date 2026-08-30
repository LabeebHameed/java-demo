package com.college;

/**
 * Model class representing a Student.
 * Serves as a Plain Old Java Object (POJO) to transfer data
 * between the database and the user interface.
 */
public class Student {
    private int id;
    private String rollNo;
    private String name;
    private String email;
    private String department;
    private String yearLevel;
    private double gpa;
    private String status;

    // Constructor for creating a new student (before database assigns an auto-generated ID)
    public Student(String rollNo, String name, String email, String department, String yearLevel, double gpa, String status) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.department = department;
        this.yearLevel = yearLevel;
        this.gpa = gpa;
        this.status = status;
    }

    // Constructor for existing student loaded from database (with ID)
    public Student(int id, String rollNo, String name, String email, String department, String yearLevel, double gpa, String status) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.department = department;
        this.yearLevel = yearLevel;
        this.gpa = gpa;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
