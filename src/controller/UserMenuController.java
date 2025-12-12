package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import project.User;
import project.BusBookingSystem;
import project.MainApp;

/**
 * UserMenu.fxml üçün Controller
 */
public class UserMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button globalTravelButton;

    @FXML
    private Button domesticTravelButton;

    @FXML
    private Button cityTravelButton;

    @FXML
    private Button buyCityCardButton;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Xoş gəldiniz, " + currentUser.name + "!");
        }
    }

    @FXML
    private void handleGlobalTravel(ActionEvent event) {
        MainApp.loadScene("/fxml/Booking.fxml", "Global Booking");
    }

    @FXML
    private void handleDomesticTravel(ActionEvent event) {
        MainApp.loadScene("/fxml/Booking.fxml","Domestic Booking");
    }

    @FXML
    private void handleCityTravel(ActionEvent event) {
        MainApp.loadScene("/fxml/CityTravel.fxml", "Şəhər səyahəti funksiyası hazırlanır...");
    }

    @FXML
    private void handleBuyCityCard(ActionEvent event) {
        MainApp.loadScene("/fxml/CityCard.fxml", "Şəhər Kartı Alış Sistemi");
    }



    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        MainApp.loadScene("/fxml/UpdateProfile.fxml", "Profeil Yenileme");
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