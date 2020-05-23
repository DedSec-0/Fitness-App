package com.example.fitnessapp;

public class Exercise {
    String exercise;
    int sets;
    String timePeriod;

    public Exercise() {
    }

    public Exercise(String exercise, int sets, String timePeriod) {
        this.exercise = exercise;
        this.sets = sets;
        this.timePeriod = timePeriod;
    }

    public String getExercise() {
        return exercise;
    }

    public int getSets() {
        return sets;
    }

    public String getTimePeriod() {
        return timePeriod;
    }
}
