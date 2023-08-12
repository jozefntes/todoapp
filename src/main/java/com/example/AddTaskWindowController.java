package com.example;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddTaskWindowController{

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField tagField;
    @FXML private CheckBox priorityCheckBox;
    @FXML private Button addButton;

    private DatabaseService databaseService = new DatabaseService();

    @FXML
    public void addTask() {
        String taskName = nameField.getText();
        LocalDate date = datePicker.getValue();
        String tag = tagField.getText();
        boolean priority = priorityCheckBox.isSelected();

        databaseService.addTask(taskName, date, tag, priority, false);
        clearFields();
        closePopupWindow();
    }

    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        tagField.clear();
        priorityCheckBox.setSelected(false);
    }

    private void closePopupWindow() {
        // Get the reference to the stage (popup window)
        Stage popupStage = (Stage) addButton.getScene().getWindow();

        // Close the stage
        popupStage.close();
    }
}