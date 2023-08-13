// Class that has the members of a Task object

package com.example;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private String taskName;
    private BooleanProperty completed;
    private String tag;
    private boolean priority;

    public Task(String taskName, boolean completed, String tag, boolean priority) {
        this.taskName = taskName;
        this.completed = new SimpleBooleanProperty(completed);
        this.tag = tag;
        this.priority = priority;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTag() {
        return tag;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }
}
