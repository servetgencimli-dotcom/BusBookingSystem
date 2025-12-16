package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import project.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class ProfileViewController {

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton childRadio;
    @FXML private TextField finField;
    @FXML private TextField seriesField;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private TextField cardField;
    @FXML private TextField expiryField;
    @FXML private TextField cvcField;

    private User currentUser;
    private ToggleGroup genderGroup;

    @FXML
    public void initialize() {
        currentUser = findCurrentUser();

        // Gender radio button group
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        childRadio.setToggleGroup(genderGroup);

        loadUserData();
    }

    private void loadUserData() {
        if (currentUser == null) return;

        nameField.setText(currentUser.name);
        ageField.setText(String.valueOf(currentUser.age));
        finField.setText(currentUser.fin);
        seriesField.setText(currentUser.idSeries);

        // Gender
        if ("K".equals(currentUser.gender)) {
            maleRadio.setSelected(true);
        } else if ("Q".equals(currentUser.gender)) {
            femaleRadio.setSelected(true);
        } else if ("U".equals(currentUser.gender)) {
            childRadio.setSelected(true);
        }

        // Card (mask et)
        if (currentUser.cardNumber != null && currentUser.cardNumber.length() >= 4) {
            cardField.setText("**** **** **** " + currentUser.cardNumber.substring(currentUser.cardNumber.length() - 4));
        }
        expiryField.setText(currentUser.cardExpiry);
        cvcField.setText("***");
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) return;

        // Validate
        String newName = nameField.getText().trim();
        if (newName.isEmpty()) {
            showAlert("❌ Xəta", "Ad boş ola bilməz!");
            return;
        }

        // Gender
        String newGender = "K";
        if (femaleRadio.isSelected()) newGender = "Q";
        else if (childRadio.isSelected()) newGender = "U";

        // Şifrə dəyişikliyi
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (!oldPass.isEmpty()) {
            if (!oldPass.equals(currentUser.password)) {
                showAlert("❌ Xəta", "Köhnə şifrə yanlışdır!");
                return;
            }

            if (newPass.isEmpty() || !newPass.equals(confirmPass)) {
                showAlert("❌ Xəta", "Yeni şifrələr uyğun gəlmir!");
                return;
            }

            currentUser.password = newPass;
        }

        // Kart məlumatları
        String newCard = cardField.getText().trim().replace(" ", "");
        if (!newCard.startsWith("****") && newCard.length() == 16) {
            currentUser.cardNumber = newCard;
        }

        String newExpiry = expiryField.getText().trim();
        if (!newExpiry.isEmpty() && newExpiry.matches("\\d{2}/\\d{2}")) {
            currentUser.cardExpiry = newExpiry;
        }

        String newCvc = cvcField.getText().trim();
        if (!newCvc.equals("***") && newCvc.length() == 3) {
            currentUser.cvc = newCvc;
        }

        // Update user
        currentUser.name = newName;
        currentUser.gender = newGender;

        // Save to database
        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET name=?, gender=?, password=?, card=?, expiry=?, cvc=? WHERE fin=?")) {

            ps.setString(1, currentUser.name);
            ps.setString(2, currentUser.gender);
            ps.setString(3, currentUser.password);
            ps.setString(4, currentUser.cardNumber);
            ps.setString(5, currentUser.cardExpiry);
            ps.setString(6, currentUser.cvc);
            ps.setString(7, currentUser.fin);

            ps.executeUpdate();

            showAlert("✅ Uğurlu", "Profil məlumatları yeniləndi!");

            // Clear password fields
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

        } catch (Exception e) {
            showAlert("❌ Xəta", "Məlumatlar saxlanılarkən xəta: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        loadUserData();
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "İstifadəçi Menyu");
    }

    private User findCurrentUser() {
        List<User> users = UserDAO.loadAllUsers();
        for (User u : users) {
            if (!u.isAdmin) {
                return u;
            }
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}