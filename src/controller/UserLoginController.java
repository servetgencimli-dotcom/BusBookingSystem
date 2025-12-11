package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import project.UserDAO;
import project.User;
import project.MainApp;


public class UserLoginController {

    @FXML
    private TextField finField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String fin = finField.getText().trim();
        String password = passwordField.getText().trim();

        if (fin.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ FIN və şifrə daxil edilməlidir!");
            return;
        }

        User user = UserDAO.loadUser(fin, password);

        if (user != null) {
            if (user.isAdmin) {
                messageLabel.setText("❌ Yanlış FIN və ya şifrə!");
                return;
            }

            SessionManager.setCurrentUser(user);
            MainApp.loadScene("/fxml/UserMenu.fxml", "İstifadəçi Menyusu - " + user.name);

        } else {
            messageLabel.setText("❌ Yanlış FIN və ya şifrə!");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/LoginMenu.fxml", "Giriş Seçimi");
    }
}