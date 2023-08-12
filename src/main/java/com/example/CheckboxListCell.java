package com.example;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CheckboxListCell<T> extends ListCell<T> {

    private final CheckBox checkBox = new CheckBox();
    private final Callback<T, BooleanProperty> selectedPropertyCallback;
    private final DatabaseService databaseService = new DatabaseService();

    public CheckboxListCell(Callback<T, BooleanProperty> selectedPropertyCallback) {
        this.selectedPropertyCallback = selectedPropertyCallback;

        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (getItem() != null) {
                selectedPropertyCallback.call(getItem()).set(newValue);

                // Update the completed status in the database
                Task task = (Task) getItem();
                databaseService.updateTaskCompletedStatus(task.getTaskName(), newValue);
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Task task = (Task) item;
            setText(task.getTaskName());
            checkBox.setSelected(task.isCompleted()); // Set checkbox based on task's completion status
            setGraphic(checkBox);
        }
    }

    public static <T> Callback<ListView<T>, ListCell<T>> forListView(Callback<T, BooleanProperty> selectedPropertyCallback) {
        return listView -> new CheckboxListCell<>(selectedPropertyCallback);
    }
}

