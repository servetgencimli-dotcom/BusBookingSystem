package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;


public class ManageRoutesController {
    @FXML private ComboBox<String> routeTypeCombo;
    @FXML private Button addRouteBtn, removeRouteBtn, renameRouteBtn, changeIntervalsBtn, backBtn;

    private String selectedRouteType;

    @FXML
    public void initialize() {
        routeTypeCombo.setItems(FXCollections.observableArrayList("Global", "Domestic", "City"));
        routeTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRouteType = newVal != null ? newVal.toLowerCase() : null;
        });
    }

    @FXML
    private void handleAddRoute() {
        if (selectedRouteType == null) {
            showAlert("Error", "Please select a route type first!");
            return;
        }
        MainApp.loadScene("/fxml/AddRoute.fxml", "Add Route");
    }

    @FXML
    private void handleRemoveRoute() {
        if (selectedRouteType == null) {
            showAlert("Error", "Please select a route type first!");
            return;
        }
        MainApp.loadScene("/fxml/RemoveRoute.fxml", "Remove Route");
    }

    @FXML
    private void handleRenameRoute() {
        if (selectedRouteType == null) {
            showAlert("Error", "Please select a route type first!");
            return;
        }
        MainApp.loadScene("/fxml/RenameRoute.fxml", "Rename Route");
    }

    @FXML
    private void handleChangeIntervals() {
        MainApp.loadScene("/fxml/ChangeIntervals.fxml", "Change Time Intervals");
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/AdminMenu.fxml", "Back");
    }

    private void openWindow(String fxml, String title) {
    }

    private void closeWindow() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
