package com.example;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;

public class TestingController {

    @FXML
    private TableView<Task> taskTableView;
    @FXML
    private TableColumn<Task, String> taskColumn;
    @FXML
    private TableColumn<Task, Boolean> completedColumn;

    @FXML
    private void initialize() {
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        completedColumn.setCellValueFactory(cellData -> cellData.getValue().completedProperty());

        completedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(completedColumn));

        List<Task> taskList = generateTaskList();
        taskTableView.getItems().addAll(taskList);
    }

    private List<Task> generateTaskList() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("Task 1", false));
        taskList.add(new Task("Task 2", true));
        taskList.add(new Task("Task 3", false));
        return taskList;
    }

    public static class Task {
        private SimpleStringProperty name;
        private SimpleBooleanProperty completed = new SimpleBooleanProperty(false); // Default is false

        public Task(String name, boolean completed) {
            this.name = new SimpleStringProperty(name);
            this.completed.set(completed);
        }

        public String getName() {
            return name.get();
        }

        public SimpleBooleanProperty completedProperty() {
            return completed;
        }
    }
}
