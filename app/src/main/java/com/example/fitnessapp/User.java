package com.example.fitnessapp;

public class User {

    private String name;
    private String email;
    private String bloodGroup;
    private String medicalReport;
    private String Category;

    public User() {
    }

    public User(String name, String email, String bloodGroup, String medicalReport, String Category) {
        this.name = name;
        this.email = email;
        this.bloodGroup = bloodGroup;
        this.medicalReport = medicalReport;
        this.Category = Category;
    }

    public String getMedicalReport() {
        return medicalReport;
    }

    public void setMedicalReport(String medicalReport) {
        this.medicalReport = medicalReport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
