package com.example;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{
    @FXML private ComboBox<LocalDate> dateChoiceBox;

    // Initialization logic for the main window
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<LocalDate> dateList = generateDateList();
        dateChoiceBox.getItems().addAll(dateList);
    }

    private List<LocalDate> generateDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 60; i++) { // Generate dates for the next 60 days
            dateList.add(today.plusDays(i));
        }
        
        return dateList;
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
