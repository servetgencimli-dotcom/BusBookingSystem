package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class ChangeIntervalsController {
    @FXML private ListView<String> intervalListView;
    @FXML private TextField newIntervalField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadIntervals();
    }

    private void loadIntervals() {
        ObservableList<String> intervals = FXCollections.observableArrayList(
                BusBookingSystem.cityIntervals
        );
        intervalListView.setItems(intervals);
    }

    @FXML
    private void handleUpdate() {
        int selectedIndex = intervalListView.getSelectionModel().getSelectedIndex();
        String newInterval = newIntervalField.getText().trim();

        if (selectedIndex < 0) {
            messageLabel.setText("Please select an interval to update!");
            return;
        }

        if (!newInterval.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}$")) {
            messageLabel.setText("Invalid format! Use HH:MM-HH:MM");
            return;
        }

        BusBookingSystem.cityIntervals[selectedIndex] = newInterval;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE cityIntervals SET intervalTime=? WHERE id=?")) {
            ps.setString(1, newInterval);
            ps.setInt(2, selectedIndex + 1);
            ps.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Interval updated successfully!");
            alert.showAndWait();

            loadIntervals();
            newIntervalField.clear();
        } catch (Exception e) {
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Back");
        Stage stage = (Stage) intervalListView.getScene().getWindow();
        stage.close();
    }
}
