package com.example.aiplanner;

import java.util.List;

public class SubTaskResponse {

    public List<SubTaskItem> subtasks;

    public static class SubTaskItem {

        public String title;
        public String description;
    }
}