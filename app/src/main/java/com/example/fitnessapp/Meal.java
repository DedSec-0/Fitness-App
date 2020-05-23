package com.example.fitnessapp;

public class Meal {

    String meal;
    String timePeriod;

    public Meal() {
    }

    public Meal(String meal, String timePeriod) {
        this.meal = meal;
        this.timePeriod = timePeriod;
    }

    public String getMeal() {
        return this.meal;
    }

    public String getTimePeriod() {
        return timePeriod;
    }
}
