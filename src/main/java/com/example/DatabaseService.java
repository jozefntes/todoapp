package com.example;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:tododatabase.db";

    public void addTask(String taskName, LocalDate date, String tag, String type) {
        String insertQuery = "INSERT INTO tasks (taskname, date, tag, type) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, taskName);
            preparedStatement.setString(2, date.toString());
            preparedStatement.setString(3, tag);
            preparedStatement.setString(4, type);

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