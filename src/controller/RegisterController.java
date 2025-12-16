package controller;

import project.BusBookingSystem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import project.Payment;
import project.User;
import project.UserDAO;
import project.MainApp;

/**
 * Register.fxml üçün Controller - YENİLƏNMİŞ VERSİYA
 */
public class RegisterController {

    @FXML
    private TextField nameField, ageField, finField, seriesField;

    @FXML
    private TextField cardField, expiryField, cvcField;

    @FXML
    private PasswordField passwordField, adminCodeField;

    @FXML
    private RadioButton maleRadio, femaleRadio, childRadio;

    @FXML
    private RadioButton userTypeRadio, adminTypeRadio;

    @FXML
    private VBox adminCodeBox;

    @FXML
    private Label messageLabel;

    @FXML
    private ToggleGroup genderGroup;

    @FXML
    private ToggleGroup userTypeGroup;

    // Navigation düymələri
    @FXML
    private Button homeNavButton;

    @FXML
    private Button loginNavButton;

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        childRadio.setToggleGroup(genderGroup);

        userTypeGroup = new ToggleGroup();
        userTypeRadio.setToggleGroup(userTypeGroup);
        adminTypeRadio.setToggleGroup(userTypeGroup);

        // Admin seçildikdə admin kod sahəsini göstər
        adminTypeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            adminCodeBox.setVisible(newVal);
            adminCodeBox.setManaged(newVal);
        });

        // Yaş 18-dən kiçik olarsa avtomatik "U" (Uşaq) seç
        ageField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int age = Integer.parseInt(newVal);
                if (age < 18) {
                    childRadio.setSelected(true);
                    genderGroup.selectToggle(childRadio);
                }
            } catch (Exception e) {
                // Invalid age, ignore
            }
        });
    }

    /**
     * Qeydiyyat əməliyyatı
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String fin = finField.getText().trim().toUpperCase();
        String series = seriesField.getText().trim();
        String password = passwordField.getText().trim();
        String card = cardField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvc = cvcField.getText().trim();

        if (name.isEmpty()) {
            showMessage("❌ Ad daxil edilməlidir!", false);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (Exception e) {
            showMessage("❌ Düzgün yaş daxil edin!", false);
            return;
        }

        String gender = "";
        if (maleRadio.isSelected()) gender = "K";
        else if (femaleRadio.isSelected()) gender = "Q";
        else if (childRadio.isSelected()) gender = "U";
        else {
            showMessage("❌ Cins seçilməlidir!", false);
            return;
        }

        if (age < 18 && !gender.equals("U")) {
            gender = "U";
        }

        boolean isAdmin = adminTypeRadio.isSelected();

        if (isAdmin) {
            if (age < 18) {
                showMessage("❌ Admin qeydiyyatı yalnız 18 yaşdan yuxarı şəxslər üçün mümkündür!", false);
                return;
            }

            String adminCode = adminCodeField.getText().trim();
            if (!adminCode.equals(BusBookingSystem.ADMIN_PASS)) {
                showMessage("❌ Yanlış Admin kodu!", false);
                return;
            }
        }

        if (!fin.matches("^[A-Z0-9]{7}$")) {
            showMessage("❌ FIN kod 7 simvol olmalıdır (böyük hərf və rəqəm)!", false);
            return;
        }

        if (!series.matches("AA\\d{7}")) {
            showMessage("❌ Seriya nömrəsi AA1234567 formatında olmalıdır!", false);
            return;
        }

        if (password.isEmpty()) {
            showMessage("❌ Şifrə daxil edilməlidir!", false);
            return;
        }

        if (!isAdmin) {
            Payment payment = new Payment();
            if (!payment.validateCardInfo(card, expiry, cvc)) {
                showMessage("❌ Kart məlumatları səhvdir!", false);
                return;
            }
        } else {
            card = "N/A";
            expiry = "N/A";
            cvc = "N/A";
        }

        User user = new User(name, gender, age, fin, series, password, card, expiry, cvc, isAdmin);
        UserDAO.saveUser(user);
        BusBookingSystem.users.add(user);

        showMessage("✅ Qeydiyyat uğurlu oldu!", true);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Geri düyməsi
     */
    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }

    /**
     * Ana səhifəyə keçid (Navigation)
     */
    @FXML
    private void goToHome(ActionEvent event) {
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }

    /**
     * Login səhifəsinə keçid (Navigation)
     */
    @FXML
    private void goToLogin(ActionEvent event) {
        MainApp.loadScene("/fxml/UserLogin.fxml", "İstifadəçi Girişi");
    }

    /**
     * Mesaj göstər
     */
    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        if (isSuccess) {
            messageLabel.setStyle(
                    "-fx-text-fill: #059669;" +
                            "-fx-font-size: 13;" +
                            "-fx-font-weight: 600;" +
                            "-fx-padding: 10;" +
                            "-fx-background-color: #d1fae5;" +
                            "-fx-background-radius: 8;"
            );
        } else {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;" +
                            "-fx-font-size: 13;" +
                            "-fx-font-weight: 600;" +
                            "-fx-padding: 10;" +
                            "-fx-background-color: #fee2e2;" +
                            "-fx-background-radius: 8;"
            );
        }
    }
}