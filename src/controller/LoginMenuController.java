package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import project.MainApp;

public class LoginMenuController {

    @FXML
    private Button userLoginButton;

    @FXML
    private Button adminLoginButton;

    @FXML
    private Button backButton;

    @FXML
    private void handleUserLogin(ActionEvent event) {
        MainApp.loadScene("/fxml/UserLogin.fxml", "İstifadəçi Girişi");
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        MainApp.loadScene("/fxml/AdminLogin.fxml", "Admin Girişi");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }
}