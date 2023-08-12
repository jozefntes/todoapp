package com.example;

import java.sql.*;
import java.time.LocalDate;

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

    public ResultSet retrieveTasks() {
        String selectQuery = "SELECT * FROM tasks";
        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }

        return resultSet;
    }
}