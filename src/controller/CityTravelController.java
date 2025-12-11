package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class CityTravelController {
    @FXML private TextField cityNameField, passengerNameField, passengerFinField;
    @FXML private TextField passengerAgeField, luggageField;
    @FXML private ComboBox<String> intervalCombo, genderCombo;
    @FXML private CheckBox useCityCardCheckbox;
    @FXML private Label priceLabel, messageLabel;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        ObservableList<String> intervals = FXCollections.observableArrayList(
                BusBookingSystem.cityIntervals
        );
        intervalCombo.setItems(intervals);
        genderCombo.setItems(FXCollections.observableArrayList("K", "Q", "U"));
    }

    @FXML
    private void handleBooking() {
        try {
            String cityName = cityNameField.getText().trim();
            String pName = passengerNameField.getText().trim();
            String pFin = passengerFinField.getText().trim();
            int pAge = Integer.parseInt(passengerAgeField.getText().trim());
            String pGender = genderCombo.getValue();
            double luggage = Double.parseDouble(luggageField.getText().trim());

            if (cityName.isEmpty() || pName.isEmpty() || pFin.isEmpty() || pGender == null) {
                messageLabel.setText("Please fill all required fields!");
                return;
            }

            CityCard usable = null;
            if (useCityCardCheckbox.isSelected()) {
                for (CityCard c : currentUser.cityCards) {
                    if (c.isUsableFor(cityName) && c.monthsLeft > 0) {
                        usable = c;
                        break;
                    }
                }
            }

            double price = 0.0;
            if (usable != null) {
                usable.useOneMonth();
                price = 0.0;
                messageLabel.setText("City card used! Trip is free.");
            } else {
                price = BusBookingSystem.payment.calculatePayment(10, true, luggage);
                BusBookingSystem.payment.processPayment(new java.util.Scanner(System.in), price, currentUser);
            }

            Passenger p = new Passenger(pName, pAge, pGender, luggage, pFin);
            Booking b = new Booking(p, 0, new java.util.Date(), price,
                    "City Travel (" + cityName + ")", intervalCombo.getValue());
            BusBookingSystem.bookingManager.addBooking(b);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("City travel booked! Price: " + price + " AZN");
            alert.showAndWait();

            handleCancel();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml","Back");
        Stage stage = (Stage) cityNameField.getScene().getWindow();
        stage.close();
    }
}
