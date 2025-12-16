package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import project.UserDAO;
import project.User;
import project.MainApp;

/**
 * UserLogin.fxml üçün Controller - YENİLƏNMİŞ VERSİYA
 */
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

    // Navigation düymələri üçün
    @FXML
    private Button homeNavButton;

    @FXML
    private Button registerNavButton;

    // Link elementləri
    @FXML
    private Label forgotPasswordLink;

    @FXML
    private Label registerLink;

    @FXML
    public void initialize() {
        // FXML-də <Label> elementlərinə onMouseClicked atributları əlavə edilməlidir
    }

    /**
     * Login əməliyyatı
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String fin = finField.getText().trim();
        String password = passwordField.getText().trim();

        if (fin.isEmpty() || password.isEmpty()) {
            showMessage("❌ FIN və şifrə daxil edilməlidir!");
            return;
        }

        User user = UserDAO.loadUser(fin, password);

        if (user != null) {
            if (user.isAdmin) {
                showMessage("❌ Yanlış FIN və ya şifrə!");
                return;
            }

            SessionManager.setCurrentUser(user);
            MainApp.loadScene("/fxml/UserMenu.fxml", "İstifadəçi Menyusu - " + user.name);

        } else {
            showMessage("❌ Yanlış FIN və ya şifrə!");
        }
    }

    /**
     * Geri düyməsi - Login menyusuna qayıt
     */
    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/LoginMenu.fxml", "Giriş Seçimi");
    }

    /**
     * Ana səhifəyə keçid (Navigation Button)
     */
    @FXML
    private void goToHome(ActionEvent event) {
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }

    /**
     * Qeydiyyat səhifəsinə keçid (Navigation Button)
     */
    @FXML
    private void goToRegister(ActionEvent event) {
        MainApp.loadScene("/fxml/Register.fxml", "Qeydiyyat");
    }

    /**
     * Şifrəni unutdum səhifəsinə keçid (Label Link - MouseEvent)
     */
    @FXML
    private void goToForgotPassword(MouseEvent event) {
        MainApp.loadScene("/fxml/ForgotPassword.fxml", "Şifrəni Unutdum");
    }

    /**
     * Şifrəni unutdum səhifəsinə keçid (ActionEvent versiyası)
     */
    @FXML
    private void goToForgotPassword(ActionEvent event) {
        MainApp.loadScene("/fxml/ForgotPassword.fxml", "Şifrəni Unutdum");
    }

    /**
     * Qeydiyyat səhifəsinə keçid (Label Link - MouseEvent)
     */
    @FXML
    private void goToRegisterLink(MouseEvent event) {
        MainApp.loadScene("/fxml/Register.fxml", "Qeydiyyat");
    }

    /**
     * Mesaj göstər
     */
    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}