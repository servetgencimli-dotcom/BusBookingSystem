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
        nameField.setText(user.name);
        genderCombo.setValue(user.gender);
        cardField.setText(user.cardNumber);
        expiryField.setText(user.cardExpiry);
        cvcField.setText(user.cvc);
    }

    @FXML
    public void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female"));
    }

    @FXML
    private void handleSave() {
        try {
            boolean anyUpdate = false;

            if (updateNameCheck.isSelected()) {
                currentUser.name = nameField.getText().trim();
                anyUpdate = true;
            }

            if (updateGenderCheck.isSelected()) {
                currentUser.gender = genderCombo.getValue();
                anyUpdate = true;
            }

            if (updatePasswordCheck.isSelected()) {
                String newPass = passwordField.getText().trim();
                if (!newPass.isEmpty()) {
                    currentUser.password = newPass;
                    anyUpdate = true;
                }
            }

            if (updateCardCheck.isSelected()) {
                String card = cardField.getText().trim();
                String exp = expiryField.getText().trim();
                String cvc = cvcField.getText().trim();

                if (BusBookingSystem.payment.validateCardInfo(card, exp, cvc)) {
                    currentUser.cardNumber = card;
                    currentUser.cardExpiry = exp;
                    currentUser.cvc = cvc;
                    anyUpdate = true;
                } else {
                    messageLabel.setText("Invalid card information!");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
            }

            if (!anyUpdate) {
                messageLabel.setText("No changes selected!");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

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

                messageLabel.setText("Profile updated successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Profile updated successfully!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "Back");
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}