// Controller class that controls the addtaskwindow.fxml

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
    private MainWindowController mainWindowController;

    // Method that sets reference to the main window controller
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    // Method called when the addButton is pressed
        // It gets the values from the fields,
        // adds the task to the database,
        // and clears the fields to close the popup window
    @FXML
    public void addTask() {
        String taskName = nameField.getText();
        LocalDate date = datePicker.getValue();
        String tag = tagField.getText();
        boolean priority = priorityCheckBox.isSelected();

        databaseService.addTask(taskName, date, tag, priority, false);
        
        // Call the method to update tags dropdown
        mainWindowController.updateTagsDropdown();
        mainWindowController.updateDateChoiceBox();

        clearFields();
        closePopupWindow();

        if (mainWindowController != null) {
            mainWindowController.displayTasksAfterAdding(date);
            mainWindowController.updateTodaysTaskListView();
        }
    }

    // Method that clears all the fields in the scene
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        tagField.clear();
        priorityCheckBox.setSelected(false);
    }

    // Method that closes the popup window to get back to the main scene
    private void closePopupWindow() {
        // Get the reference to the stage (popup window)
        Stage popupStage = (Stage) addButton.getScene().getWindow();

        // Close the stage
        popupStage.close();
    }
}