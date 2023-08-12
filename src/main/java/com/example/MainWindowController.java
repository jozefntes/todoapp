package com.example;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainWindowController implements Initializable{
    @FXML private ComboBox<LocalDate> dateChoiceBox;
    @FXML private Label displayDateLabel;
    @FXML private ListView<Task> taskListView;
    @FXML private TitledPane tagsCollapsibleDropdown;
    @FXML private VBox tagsVBox;

    DatabaseService databaseService = new DatabaseService();
    private Set<String> selectedTags = new HashSet<>();

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

        List<String> uniqueTags = retrieveUniqueTags();
        uniqueTags.forEach(tag -> {
            CheckBox checkBox = new CheckBox(tag);
            checkBox.setOnAction(event -> handleTagCheckboxClicked(checkBox.getText(), checkBox.isSelected()));
            tagsVBox.getChildren().add(checkBox);
        });
    }

    private void handleTagCheckboxClicked(String tag, boolean isSelected) {
        // Handle tag checkbox click event, e.g., perform an action based on the tag selection
        if (isSelected) {
            selectedTags.add(tag);
        } else {
            selectedTags.remove(tag);
        }
        updateTaskListView(dateChoiceBox.getValue());
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

    public void displayTasksAfterAdding(LocalDate selectedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = selectedDate.format(formatter);
        displayDateLabel.setText(formattedDate);

        updateTaskListView(selectedDate);
    }

    public void updateTaskListView(LocalDate date) {
        // Clear previous items
        taskListView.getItems().clear(); 
        // Use today's date if no date is selected
        LocalDate selectedDate = date != null ? date : LocalDate.now(); 
        List<Task> tasks = retrieveTasksForDate(selectedDate);

        if (!selectedTags.isEmpty()) {
        // Filter tasks by selected tags
        tasks = tasks.stream()
            .filter(task -> selectedTags.contains(task.getTag()))
            .collect(Collectors.toList());
        }

        taskListView.getItems().addAll(tasks);
        taskListView.setCellFactory(CheckboxListCell.forListView(task -> task.completedProperty()));
    }

    private List<String> retrieveUniqueTags() {
        return databaseService.retrieveUniqueTags(); // Modify the method in DatabaseService to retrieve unique tags
    }

    public void updateTagsDropdown() {
        List<String> uniqueTags = retrieveUniqueTags();
        List<CheckBox> existingCheckBoxes = new ArrayList<>();

        // Remove existing checkboxes from VBox and store them
        for (Node node : tagsVBox.getChildren()) {
            if (node instanceof CheckBox) {
                existingCheckBoxes.add((CheckBox) node);
            }
        }

        tagsVBox.getChildren().clear(); // Clear all existing checkboxes

        uniqueTags.forEach(tag -> {
            CheckBox checkBox = new CheckBox(tag);
            checkBox.setOnAction(event -> handleTagCheckboxClicked(tag, checkBox.isSelected()));

            // If the tag checkbox was displayed before, reuse it
            for (CheckBox existingCheckBox : existingCheckBoxes) {
                if (existingCheckBox.getText().equals(tag)) {
                    checkBox.setSelected(existingCheckBox.isSelected());
                    break;
                }
            }

            tagsVBox.getChildren().add(checkBox);
        });
    }
}
