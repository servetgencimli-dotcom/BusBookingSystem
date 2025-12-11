package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class RemoveRouteController {
    @FXML private ComboBox<String> routeTypeCombo;
    @FXML private ListView<String> routeListView;
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
    private void handleRemove() {
        String selectedRoute = routeListView.getSelectionModel().getSelectedItem();
        String type = routeTypeCombo.getValue();

        if (selectedRoute == null) {
            messageLabel.setText("Please select a route to remove!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setContentText("Are you sure you want to remove: " + selectedRoute + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DB.connect()) {
                    conn.setAutoCommit(false);

                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM routes WHERE routeName=?")) {
                        ps.setString(1, selectedRoute);
                        ps.executeUpdate();
                    }

                    BusBookingSystem.busManager.removeBus(selectedRoute);

                    if (type.equalsIgnoreCase("global")) {
                        BusBookingSystem.globalRoutes.remove(selectedRoute);
                    } else {
                        BusBookingSystem.domesticRoutes.remove(selectedRoute);
                    }

                    conn.commit();

                    messageLabel.setText("Route removed successfully!");
                    loadRoutes();
                } catch (SQLException e) {
                    messageLabel.setText("Database error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/ManageRoutes.fxml","Back");
        Stage stage = (Stage) routeListView.getScene().getWindow();
        stage.close();
    }
}
