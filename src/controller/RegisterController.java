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
 * Register.fxml üçün Controller
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

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        childRadio.setToggleGroup(genderGroup);

        userTypeGroup = new ToggleGroup();
        userTypeRadio.setToggleGroup(userTypeGroup);
        adminTypeRadio.setToggleGroup(userTypeGroup);

        adminTypeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            adminCodeBox.setVisible(newVal);
            adminCodeBox.setManaged(newVal);
        });

        ageField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int age = Integer.parseInt(newVal);
                if (age < 18) {
                    childRadio.setSelected(true);
                    genderGroup.selectToggle(childRadio);
                }
            } catch (Exception e) {
            }
        });
    }

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
            messageLabel.setText("❌ Ad daxil edilməlidir!");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (Exception e) {
            messageLabel.setText("❌ Düzgün yaş daxil edin!");
            return;
        }

        String gender = "";
        if (maleRadio.isSelected()) gender = "K";
        else if (femaleRadio.isSelected()) gender = "Q";
        else if (childRadio.isSelected()) gender = "U";
        else {
            messageLabel.setText("❌ Cins seçilməlidir!");
            return;
        }

        if (age < 18 && !gender.equals("U")) {
            gender = "U";
        }

        boolean isAdmin = adminTypeRadio.isSelected();

        if (isAdmin) {
            if (age < 18) {
                messageLabel.setText("❌ Admin qeydiyyatı yalnız 18 yaşdan yuxarı şəxslər üçün mümkündür!");
                return;
            }

            String adminCode = adminCodeField.getText().trim();
            if (!adminCode.equals(BusBookingSystem.ADMIN_PASS)) {
                messageLabel.setText("❌ Yanlış Admin kodu!");
                return;
            }
        }

        if (!fin.matches("^[A-Z0-9]{7}$")) {
            messageLabel.setText("❌ FIN kod 7 simvol olmalıdır (böyük hərf və rəqəm)!");
            return;
        }

        if (!series.matches("AA\\d{7}")) {
            messageLabel.setText("❌ Seriya nömrəsi AA1234567 formatında olmalıdır!");
            return;
        }

        if (password.isEmpty()) {
            messageLabel.setText("❌ Şifrə daxil edilməlidir!");
            return;
        }

        if (!isAdmin) {
            Payment payment = new Payment();
            if (!payment.validateCardInfo(card, expiry, cvc)) {
                messageLabel.setText("❌ Kart məlumatları səhvdir!");
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

        messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        messageLabel.setText("✅ Qeydiyyat uğurlu oldu!");

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

    @FXML
    private void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }
}