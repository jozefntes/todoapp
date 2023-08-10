// Class for storing user tasks

package com.example;

public class Task {
    private String name;
    private String tag;
    private String due;
    private String type;
    private boolean complete = false;
    
    public Task(String name, String tag, String due, String type) {
        this.name = name;
        this.tag = tag;
        this.due = due;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getDue() {
        return due;
    }

    public String getType() {
        return type;
    }

    public boolean getComplete() {
        return complete;
    }
}
