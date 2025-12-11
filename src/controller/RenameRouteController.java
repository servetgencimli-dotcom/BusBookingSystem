package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;


public class RenameRouteController {
    @FXML private ComboBox<String> routeTypeCombo;
    @FXML private ListView<String> routeListView;
    @FXML private TextField newNameField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        routeTypeCombo.setItems(FXCollections.observableArrayList("Global", "Domestic"));
    }

    @FXML
    private void loadRoutes() {
        String type = routeTypeCombo.getValue();
        if (type == null) return;

        ObservableList<String> routes = FXCollections.observableArrayList();

        if (type.equalsIgnoreCase("global")) {
            routes.addAll(BusBookingSystem.globalRoutes.keySet());
        } else {
            routes.addAll(BusBookingSystem.domesticRoutes.keySet());
        }

        routeListView.setItems(routes);
    }

    @FXML
    private void handleRename() {
        String oldName = routeListView.getSelectionModel().getSelectedItem();
        String newName = newNameField.getText().trim();
        String type = routeTypeCombo.getValue();

        if (oldName == null) {
            messageLabel.setText("Please select a route!");
            return;
        }

        if (newName.isEmpty() || !newName.contains("-")) {
            messageLabel.setText("Invalid new name! Use format: Baku-CityName");
            return;
        }

        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false);

            int distance;
            if (type.equalsIgnoreCase("global")) {
                distance = BusBookingSystem.globalRoutes.get(oldName);
            } else {
                distance = BusBookingSystem.domesticRoutes.get(oldName);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE routes SET routeName=? WHERE routeName=?")) {
                ps.setString(1, newName);
                ps.setString(2, oldName);
                ps.executeUpdate();
            }

            BusBookingSystem.busManager.removeBus(oldName);
            BusBookingSystem.busManager.addBus(newName, new Bus(newName));

            if (type.equalsIgnoreCase("global")) {
                BusBookingSystem.globalRoutes.remove(oldName);
                BusBookingSystem.globalRoutes.put(newName, distance);
            } else {
                BusBookingSystem.domesticRoutes.remove(oldName);
                BusBookingSystem.domesticRoutes.put(newName, distance);
            }

            conn.commit();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Route renamed successfully!");
            alert.showAndWait();

            loadRoutes();
            newNameField.clear();
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Back");
        Stage stage = (Stage) newNameField.getScene().getWindow();
        stage.close();
    }
}
