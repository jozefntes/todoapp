package com.example;

import java.io.IOException;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{
    // Initialization logic for the main window
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // Method to open the popup window to add task
    @FXML
    public void addTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addtaskwindow.fxml"));
            Parent root = loader.load();

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
}
