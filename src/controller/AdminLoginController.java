package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import project.User;
import project.UserDAO;
import project.MainApp;

/**
 * AdminLogin.fxml üçün Controller
 */
public class AdminLoginController {

    @FXML
    private TextField finField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String fin = finField.getText().trim();
        String password = passwordField.getText().trim();

        if (fin.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ Admin FIN və şifrə daxil edilməlidir!");
            return;
        }

        User admin = UserDAO.loadUser(fin, password);

        if (admin != null && admin.isAdmin) {
            SessionManager.setCurrentUser(admin);
            MainApp.loadScene("/fxml/AdminMenu.fxml", "Admin Menyusu");
        } else {
            messageLabel.setText("❌ Yanlış admin məlumatları!");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/LoginMenu.fxml", "Giriş Seçimi");
    }
}