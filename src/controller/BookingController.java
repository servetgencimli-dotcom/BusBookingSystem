package controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class BookingController {
    @FXML private ComboBox<String> travelTypeCombo, routeCombo, intervalCombo;
    @FXML private ComboBox<String> genderCombo, floorCombo, seatCombo;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> ticketSpinner;
    @FXML private TextField passengerNameField, passengerFinField, passengerAgeField, luggageField;
    @FXML private Label priceLabel, messageLabel;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        travelTypeCombo.setItems(FXCollections.observableArrayList("Global", "Domestic"));
        genderCombo.setItems(FXCollections.observableArrayList("K", "Q", "U"));
        floorCombo.setItems(FXCollections.observableArrayList("1", "2"));

        ObservableList<String> intervals = FXCollections.observableArrayList(
                BusBookingSystem.cityIntervals
        );
        intervalCombo.setItems(intervals);

        travelTypeCombo.setOnAction(e -> loadRoutes());

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1);
        ticketSpinner.setValueFactory(valueFactory);
    }

    private void loadRoutes() {
        String type = travelTypeCombo.getValue();
        if (type == null) return;

        ObservableList<String> routes = FXCollections.observableArrayList();

        if (type.equalsIgnoreCase("global")) {
            routes.addAll(BusBookingSystem.globalRoutes.keySet());
        } else {
            routes.addAll(BusBookingSystem.domesticRoutes.keySet());
        }

        routeCombo.setItems(routes);
    }

    @FXML
    private void showSeats() {
        String route = routeCombo.getValue();
        String floor = floorCombo.getValue();

        if (route == null || floor == null) {
            messageLabel.setText("Please select route and floor first!");
            return;
        }

        Bus bus = BusBookingSystem.busManager.getBus(route);
        if (bus == null) {
            messageLabel.setText("Bus not found!");
            return;
        }

        int floorNum = Integer.parseInt(floor);
    }

    @FXML
    private void handleBooking() {
        try {
            String route = routeCombo.getValue();
            String type = travelTypeCombo.getValue();
            String pName = passengerNameField.getText().trim();
            String pFin = passengerFinField.getText().trim();
            int pAge = Integer.parseInt(passengerAgeField.getText().trim());
            String pGender = genderCombo.getValue();
            double luggage = Double.parseDouble(luggageField.getText().trim());

            if (route == null || pName.isEmpty() || pFin.isEmpty() || pGender == null) {
                messageLabel.setText("Please fill all required fields!");
                return;
            }

            int distance = type.equalsIgnoreCase("global") ?
                    BusBookingSystem.globalRoutes.get(route) :
                    BusBookingSystem.domesticRoutes.get(route);

            double price = BusBookingSystem.payment.calculatePayment(distance, true, luggage);
            BusBookingSystem.payment.processPayment(new java.util.Scanner(System.in), price, currentUser);

            Passenger p = new Passenger(pName, pAge, pGender, luggage, pFin);
            Booking b = new Booking(p, 0, new java.util.Date(), price, route,
                    intervalCombo.getValue());
            BusBookingSystem.bookingManager.addBooking(b);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Booking completed successfully! Price: " + price + " AZN");
            alert.showAndWait();

            handleCancel();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml","Back");
        Stage stage = (Stage) passengerNameField.getScene().getWindow();
        stage.close();
    }
}