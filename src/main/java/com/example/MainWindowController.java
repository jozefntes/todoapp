package com.example;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{
    @FXML private ComboBox<LocalDate> dateChoiceBox;
    @FXML private Label displayDateLabel;
    @FXML private ListView<Task> taskListView;

    DatabaseService databaseService = new DatabaseService();

    // Initialization logic for the main window
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<LocalDate> dateList = generateDateList();
        dateChoiceBox.getItems().addAll(dateList);
        // Set a custom cell factory to display formatted dates in the ComboBox
        dateChoiceBox.setCellFactory(param -> new DateListCell());
        dateChoiceBox.setButtonCell(new DateListCell());
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = today.format(formatter);
        displayDateLabel.setText(formattedDate);

        updateTaskListView(today);
    }

    private List<Task> retrieveTasksForDate(LocalDate date) {
        // Fetch tasks from your database based on the date
        // Return a list of Task objects
        return databaseService.retrieveTasksForDate(date);
    }
    
    private List<LocalDate> generateDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 60; i++) { // Generate dates for the next 60 days
            dateList.add(today.plusDays(i));
        }
        
        return dateList;
    }

    // Custom ListCell to format the dates in the ComboBox
    private class DateListCell extends ListCell<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        @Override
        protected void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);

            if (empty || date == null) {
                setText(null);
            } else {
                setText(date.format(formatter));
            }
        }
    }

    // Method to open the popup window to add task
    @FXML
    private void addTask() {
        // Open the popup window
        openPopup();
    }

    private void openPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addtaskwindow.fxml"));
            Parent root = loader.load();

            // Set the reference
            AddTaskWindowController addTaskController = loader.getController();
            addTaskController.setMainWindowController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Task");
            popupStage.setScene(new Scene(root));

            // Set the popup window to be not resizable
            popupStage.setResizable(false);

            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void displayTasksFromDate() {
        LocalDate selectedDate = dateChoiceBox.getSelectionModel().getSelectedItem();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = selectedDate.format(formatter);
        displayDateLabel.setText(formattedDate);

        updateTaskListView(selectedDate);
    }

    public void updateTaskListView(LocalDate date) {
        taskListView.getItems().clear(); // Clear previous items
        List<Task> tasks = retrieveTasksForDate(date);
        taskListView.getItems().addAll(tasks);
        taskListView.setCellFactory(CheckboxListCell.forListView(task -> task.completedProperty()));
    }
}
