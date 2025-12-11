package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import project.BusBookingSystem;
import project.MainApp;

/**
 * AdminMenu.fxml üçün Controller
 */
public class AdminMenuController {

    @FXML
    private Button globalRoutesButton;

    @FXML
    private Button domesticRoutesButton;

    @FXML
    private Button cityRoutesButton;

    @FXML
    private Button allBookingsButton;

    @FXML
    private Button viewUsersButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void handleGlobalRoutes(ActionEvent event) {
        showAlert("Məlumat", "Beynəlxalq marşrutlar idarəetməsi hazırlanır...");
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Manage sehifesi acilir");
    }

    @FXML
    private void handleDomesticRoutes(ActionEvent event) {
        showAlert("Məlumat", "Daxili marşrutlar idarəetməsi hazırlanır...");
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Manage sehifesi acilir");
    }

    @FXML
    private void handleCityRoutes(ActionEvent event) {
        showAlert("Məlumat", "Şəhər marşrutları idarəetməsi hazırlanır...");
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Manage sehifesi acilir");
    }

    @FXML
    private void handleAllBookings(ActionEvent event) {
        BusBookingSystem.bookingManager.displayAllBookings();
        showAlert("Məlumat", "Bütün rezervasiyalar console-da göstərildi.");
    }

    @FXML
    private void handleViewUsers(ActionEvent event) {
        BusBookingSystem.viewUsers();
        showAlert("Məlumat", "İstifadəçilər console-da göstərildi.");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}