package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.*;
import java.sql.*;

public class UpdateProfileController {
    @FXML private TextField nameField, cardField, expiryField, cvcField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private CheckBox updateNameCheck, updateGenderCheck, updatePasswordCheck, updateCardCheck;
    @FXML private Label messageLabel;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            nameField.setText(user.name != null ? user.name : "");
            genderCombo.setValue(user.gender != null ? user.gender : "");
            cardField.setText(user.cardNumber != null ? user.cardNumber : "");
            expiryField.setText(user.cardExpiry != null ? user.cardExpiry : "");
            cvcField.setText(user.cvc != null ? user.cvc : "");
        }
    }

    @FXML
    public void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList("K", "Q", "U"));

        // User məlumatını SessionManager-dən yüklə
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            setUser(user);
        }
    }

    @FXML
    private void handleSave() {
        try {
            // 1. User yoxlaması
            if (currentUser == null) {
                currentUser = SessionManager.getCurrentUser();
                if (currentUser == null) {
                    messageLabel.setText("❌ İstifadəçi məlumatı tapılmadı!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
            }

            boolean anyUpdate = false;

            // 2. Ad dəyişikliyi
            if (updateNameCheck.isSelected()) {
                String newName = nameField.getText().trim();
                if (newName.isEmpty()) {
                    messageLabel.setText("❌ Ad boş ola bilməz!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
                currentUser.name = newName;
                anyUpdate = true;
            }

            // 3. Cins dəyişikliyi
            if (updateGenderCheck.isSelected()) {
                String newGender = genderCombo.getValue();
                if (newGender == null || newGender.isEmpty()) {
                    messageLabel.setText("❌ Cins seçin!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
                currentUser.gender = newGender;
                anyUpdate = true;
            }

            // 4. Şifrə dəyişikliyi
            if (updatePasswordCheck.isSelected()) {
                String newPass = passwordField.getText().trim();
                if (newPass.isEmpty()) {
                    messageLabel.setText("❌ Şifrə boş ola bilməz!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
                if (newPass.length() < 6) {
                    messageLabel.setText("❌ Şifrə ən azı 6 simvol olmalıdır!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
                currentUser.password = newPass;
                anyUpdate = true;
            }

            // 5. Kart məlumatları dəyişikliyi
            if (updateCardCheck.isSelected()) {
                String card = cardField.getText().trim();
                String exp = expiryField.getText().trim();
                String cvc = cvcField.getText().trim();

                if (card.isEmpty() || exp.isEmpty() || cvc.isEmpty()) {
                    messageLabel.setText("❌ Bütün kart məlumatlarını doldurun!");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }

                // Kart validasiyası
                boolean isValid = false;
                try {
                    if (BusBookingSystem.payment != null) {
                        isValid = BusBookingSystem.payment.validateCardInfo(card, exp, cvc);
                    } else {
                        // Default validasiya
                        isValid = card.matches("\\d{16}") &&
                                exp.matches("\\d{2}/\\d{2}") &&
                                cvc.matches("\\d{3}");
                    }
                } catch (Exception e) {
                    // Əgər payment sistemi işləməsə, sadə validasiya
                    isValid = card.matches("\\d{16}") &&
                            exp.matches("\\d{2}/\\d{2}") &&
                            cvc.matches("\\d{3}");
                }

                if (isValid) {
                    currentUser.cardNumber = card;
                    currentUser.cardExpiry = exp;
                    currentUser.cvc = cvc;
                    anyUpdate = true;
                } else {
                    messageLabel.setText("❌ Kart məlumatları düzgün deyil!\nFormat: 16 rəqəm, MM/YY, 3 rəqəm CVC");
                    messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    return;
                }
            }

            // 6. Heç bir dəyişiklik seçilməyibsə
            if (!anyUpdate) {
                messageLabel.setText("⚠️ Heç bir dəyişiklik seçilməyib!");
                messageLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                return;
            }

            // 7. Database-ə yaz
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

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    // SessionManager-i yenilə
                    SessionManager.setCurrentUser(currentUser);

                    messageLabel.setText("✅ Profil uğurla yeniləndi!");
                    messageLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");

                    // Uğur mesajı
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("✅ Uğurlu Yeniləmə");
                    alert.setHeaderText("Profil Yeniləndi!");

                    StringBuilder changes = new StringBuilder();
                    changes.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    changes.append("📋 Yenilənən Məlumatlar:\n");
                    changes.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

                    if (updateNameCheck.isSelected()) {
                        changes.append("👤 Ad: ").append(currentUser.name).append("\n");
                    }
                    if (updateGenderCheck.isSelected()) {
                        changes.append("⚧️ Cins: ").append(currentUser.gender).append("\n");
                    }
                    if (updatePasswordCheck.isSelected()) {
                        changes.append("🔒 Şifrə yeniləndi\n");
                    }
                    if (updateCardCheck.isSelected()) {
                        changes.append("💳 Kart məlumatları yeniləndi\n");
                    }

                    changes.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    changes.append("Dəyişikliklər yadda saxlanıldı! ✅");

                    alert.setContentText(changes.toString());
                    alert.showAndWait();

                    // Checkbox-ları sıfırla
                    updateNameCheck.setSelected(false);
                    updateGenderCheck.setSelected(false);
                    updatePasswordCheck.setSelected(false);
                    updateCardCheck.setSelected(false);
                    passwordField.clear();

                } else {
                    messageLabel.setText("⚠️ Profil yenilənmədi!");
                    messageLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                }

            } catch (SQLException e) {
                messageLabel.setText("❌ Database xətası: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                e.printStackTrace();
            }

        } catch (Exception e) {
            messageLabel.setText("❌ Xəta: " + (e.getMessage() != null ? e.getMessage() : "Naməlum xəta"));
            messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "Back");
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}