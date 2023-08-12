package com.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:tododatabase.db";

    public void addTask(String taskName, LocalDate date, String tag, boolean priority, boolean complete) {
        String insertQuery = "INSERT INTO tasks (taskname, date, tag, priority, complete) VALUES (?, ?, ?, ?, ?)";
        int priorityInt = priority ? 1 : 0;
        int completeInt = complete ? 1 : 0;

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, taskName);
            preparedStatement.setString(2, date.toString());
            preparedStatement.setString(3, tag);
            preparedStatement.setInt(4, priorityInt);
            preparedStatement.setInt(5, completeInt);


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    public List<Task> retrieveTasksForDate(LocalDate date) {
        List<Task> tasks = new ArrayList<>();

        String selectQuery = "SELECT * FROM tasks WHERE date = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, date.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String taskName = resultSet.getString("taskname");
                boolean completed = resultSet.getInt("complete") == 1;
                String tag = resultSet.getString("tag");
                boolean priority = resultSet.getInt("priority") == 1;
                Task task = new Task(taskName, completed, tag, priority);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }

        return tasks;
    }

    public void updateTaskCompletedStatus(String taskName, boolean completed) {
        String updateQuery = "UPDATE tasks SET complete = ? WHERE taskname = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            int completeInt = completed ? 1 : 0;

            preparedStatement.setInt(1, completeInt);
            preparedStatement.setString(2, taskName);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    public List<String> retrieveUniqueTags() {
        List<String> tags = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT tag FROM tasks";

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }

        return tags;
    }

}