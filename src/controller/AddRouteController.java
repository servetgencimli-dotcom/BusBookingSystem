package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import project.MainApp;

public class AddRouteController {

    @FXML
    private TextField routeNameField;

    @FXML
    private TextField distanceField;

    @FXML
    private ComboBox<String> routeTypeCombo;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // ComboBox-a növləri əlavə edin
        routeTypeCombo.getItems().addAll("Highway", "City Road", "Mountain Road");
    }

    @FXML
    private void handleAddRoute() {
        String routeName = routeNameField.getText().trim();
        String distance = distanceField.getText().trim();
        String routeType = routeTypeCombo.getValue();

        if (routeName.isEmpty() || distance.isEmpty() || routeType == null) {
            messageLabel.setText("❌ Please fill all fields!");
            return;
        }

        try {
            double dist = Double.parseDouble(distance);
            if (dist <= 0) {
                messageLabel.setText("❌ Distance must be positive!");
                return;
            }

            // Route əlavə etmə məntiqiniz buraya
            messageLabel.setStyle("-fx-text-fill: #38a169;");
            messageLabel.setText("✅ Route added successfully!");

            // Clear fields
            clearFields();

        } catch (NumberFormatException e) {
            messageLabel.setText("❌ Please enter a valid number for distance!");
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Back");
        clearFields();
    }

    // Hover effektləri
    @FXML
    private void onButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.play();
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    private void clearFields() {
        routeNameField.clear();
        distanceField.clear();
        routeTypeCombo.setValue(null);
        messageLabel.setText("");
    }
}