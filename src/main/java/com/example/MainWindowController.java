// Controller class that controles the mainwindow.fxml

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
    @FXML private VBox tagsVBox;
    @FXML private ListView<Task> highPriorityListView;
    @FXML private ListView<Task> noPriorityListView;
    @FXML private Label completionLabel;
    @FXML private ProgressBar completionBar;

    DatabaseService databaseService = new DatabaseService();
    private Set<String> selectedTags = new HashSet<>();

    // Initialization logic for the main window
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Updates the ChoiceBox to have from earliest date in database to 60 days from today
        updateDateChoiceBox();
        
        // Displays today's date as default in the main taskListView header
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = today.format(formatter);
        displayDateLabel.setText(formattedDate);

        // Set the default selected date to today
        dateChoiceBox.getSelectionModel().select(today);

        // Displays today's tasks in the main taskListView as default
        updateTaskListView(today);
        // Display today's tasks in the todays tasks high and no priority listViews
        updateTodaysTaskListView();

        // Get unique tags to populate the collapsible dropdown for tags
        List<String> uniqueTags = retrieveUniqueTags();
        uniqueTags.forEach(tag -> {
            CheckBox checkBox = new CheckBox(tag);
            checkBox.setOnAction(event -> handleTagCheckboxClicked(checkBox.getText(), checkBox.isSelected()));
            tagsVBox.getChildren().add(checkBox);
        });
    }

    // Method that handles a tag selection
        // If a tag is selected, it will only display the tasks with the selected tag and date
        // If more tags are selected, it will display those tasks too
        // If none are selected, it will display all the tasks for the selected date
    private void handleTagCheckboxClicked(String tag, boolean isSelected) {
        if (isSelected) {
            selectedTags.add(tag);
        } else {
            selectedTags.remove(tag);
        }
        // Update both the main taskListView and the high and no priority listViews
        updateTaskListView(dateChoiceBox.getValue());
        updateTodaysTaskListView();
    }

    // Method that generates the dates a list of LocalDate
        // This is used in the initialize method to populate the ComboBox to chose a display date
    private List<LocalDate> generateDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Retrieve the earliest date from the tasks in the database
        LocalDate earliestDate = databaseService.retrieveEarliestTaskDate();

        // Calculate the ending point (60 days from today)
        LocalDate endDate = today.plusDays(60);

        // Use the earliest date as the starting point if available
        LocalDate startDate = earliestDate != null ? earliestDate : today;

        // Generate dates within the range of the earliest date to 60 days from today
        while (!startDate.isAfter(endDate)) {
            dateList.add(startDate);
            startDate = startDate.plusDays(1);
        }

        return dateList;
    }

    // Method to format the dates in the ComboBox
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

    // Method that opens the popup window to add a task
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

    // Method that is called when a date is selected from the comboBox
        // It displays the selected date in the header
        // and updates the main taskListView with the selected date tasks
    @FXML
    private void displayTasksFromDate() {
        LocalDate selectedDate = dateChoiceBox.getSelectionModel().getSelectedItem();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = selectedDate.format(formatter);
        displayDateLabel.setText(formattedDate);

        updateTaskListView(selectedDate);
    }

    // Method that 
        // Displays the selected date in the header
        // and updates the main taskListView with the selected date tasks
    // This is method is called from the AddTaskWindowController class
    public void displayTasksAfterAdding(LocalDate selectedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = selectedDate.format(formatter);
        displayDateLabel.setText(formattedDate);

        updateTaskListView(selectedDate);
    }

    // Method that updates the dateChoiceBox
    public void updateDateChoiceBox() {
        if (dateChoiceBox != null) {
            dateChoiceBox.getItems().clear();
        }
        // Generates a list of dates for the ComboBox
        List<LocalDate> dateList = generateDateList();
        dateChoiceBox.getItems().addAll(dateList);
        // Set a custom cell factory to display formatted dates in the ComboBox
        dateChoiceBox.setCellFactory(param -> new DateListCell());
        dateChoiceBox.setButtonCell(new DateListCell());
    }

    // Method that updates the taskListView with the passed date
        // It displays the tasks from the passed date in the main taskListView
        // and adds a checkbox that is bound to the task
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

        // Add listeners to task completion status properties
        tasks.forEach(task -> task.completedProperty().addListener((observable, oldValue, newValue) -> {
            updateCompletionInfo();
        }));

        // Update completion info after updating taskListView
        updateCompletionInfo();
    }

    // Method that updates the high and no priority listViews
        // It displays the tasks with priority in the high priority collapsible dropdown,
        // displays the tasks with no priority in the no priority collapsible dropdown,
        // and adds a checkbox that is bound to the task in both listViews
    public void updateTodaysTaskListView() {
        LocalDate today = LocalDate.now();

        List<Task> highPriorityTasks = retrieveTasksForTodayHighPriority();
        List<Task> noPriorityTasks = retrieveTasksForTodayNoPriority();

        // Update high priority tasks list view
        if (highPriorityListView != null) {
            highPriorityListView.getItems().clear();
            highPriorityListView.getItems().addAll(highPriorityTasks);
            highPriorityListView.setCellFactory(CheckboxListCell.forListView(task -> task.completedProperty()));
        }

        // Update no priority tasks list view
        if (noPriorityListView != null) {
            noPriorityListView.getItems().clear();
            noPriorityListView.getItems().addAll(noPriorityTasks);
            noPriorityListView.setCellFactory(CheckboxListCell.forListView(task -> task.completedProperty()));
        }

        // Update main task list view if the selected date is today
        if (dateChoiceBox.getValue() == null || dateChoiceBox.getValue().equals(today)) {
            updateTaskListView(today);
        }
    }

    // Method to calculate completion percentage and update ProgressBar
    private void updateCompletionInfo() {
        List<Task> tasks = taskListView.getItems();
        if (tasks.isEmpty()) {
            completionLabel.setText("Completion: 0%");
            completionBar.setProgress(0.0);
        } else {
            long completedCount = tasks.stream().filter(Task::isCompleted).count();
            double completionPercentage = (completedCount * 100.0) / tasks.size();

            completionLabel.setText(String.format("Completion: %.2f%%", completionPercentage));
            completionBar.setProgress(completionPercentage / 100.0);
        }
    }

        // Method that retrieves tasks from the DatabaseService for a specific date
    private List<Task> retrieveTasksForDate(LocalDate date) {
        // Fetch tasks from your database based on the date
        // Return a list of Task objects
        return databaseService.retrieveTasksForDate(date);
    }

    // Method that retrieves tasks from the DatabaseService for today with high priority
    private List<Task> retrieveTasksForTodayHighPriority() {
        return databaseService.retrieveTasksForTodayHighPriority();
    }

    // Method that retrieves tasks from the DatabaseService for today with no priority
    private List<Task> retrieveTasksForTodayNoPriority() {
        return databaseService.retrieveTasksForTodayNoPriority();
    }

    // Method that retrieves the unique Tags from all the tasks in the database
    private List<String> retrieveUniqueTags() {
        return databaseService.retrieveUniqueTags();
    }

    // Method that updates the tags dropdown
        // This is used when a new task is added
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
