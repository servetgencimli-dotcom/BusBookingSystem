package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLoginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        animateButtonsOnLoad();
    }

    @FXML
    private void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Daxil ol");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Qeydiyyat");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void onButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Böyütmə animasiyası
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.play();

        // Yuxarı qaldırma effekti
        TranslateTransition translate = new TranslateTransition(Duration.millis(200), button);
        translate.setToY(-3);
        translate.play();
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Normal ölçüyə qayıtma
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();

        // Normal mövqeyə qayıtma
        TranslateTransition translate = new TranslateTransition(Duration.millis(200), button);
        translate.setToY(0);
        translate.play();
    }

    // Səhifə yüklənəndə düymələri animasiya et
    private void animateButtonsOnLoad() {
        animateButton(loginButton, 0);
        animateButton(registerButton, 100);
        animateButton(exitButton, 200);
    }

    private void animateButton(Button button, long delay) {
        button.setOpacity(0);
        button.setTranslateY(20);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.millis(600), button);
        fade.setDelay(Duration.millis(delay));
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(600), button);
        translate.setDelay(Duration.millis(delay));
        translate.setFromY(20);
        translate.setToY(0);

        fade.play();
        translate.play();
    }
}