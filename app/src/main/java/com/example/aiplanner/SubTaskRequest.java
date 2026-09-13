package com.example.aiplanner;

public class SubTaskRequest {

    private String title;
    private String description;
    private int duration_minutes;
    private int priority;

    public SubTaskRequest(
            String title,
            String description,
            int duration_minutes,
            int priority) {

        this.title = title;
        this.description = description;
        this.duration_minutes = duration_minutes;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration_minutes() {
        return duration_minutes;
    }

    public int getPriority() {
        return priority;
    }
}