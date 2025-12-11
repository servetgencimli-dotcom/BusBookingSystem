package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;




public class ReceiptController {
    @FXML private Label passengerLabel, routeLabel, dateLabel, intervalLabel;
    @FXML private Label seatLabel, luggageLabel, priceLabel;
    @FXML private TextArea receiptTextArea;

    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
        displayReceipt();
    }

    private void displayReceipt() {
        if (booking == null) return;

        passengerLabel.setText(booking.getPassenger().getName());
        routeLabel.setText(booking.getRouteName());
        dateLabel.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(booking.getTravelDate()));
        intervalLabel.setText(booking.getInterval());
        seatLabel.setText(String.valueOf(booking.getBusNo()));
        priceLabel.setText(String.format("%.2f AZN", booking.getPrice()));

        StringBuilder receipt = new StringBuilder();
        receipt.append("==========================================\n");
        receipt.append("           BOOKING RECEIPT\n");
        receipt.append("==========================================\n\n");
        receipt.append("Passenger: ").append(booking.getPassenger().getName()).append("\n");
        receipt.append("FIN: ").append(booking.getPassenger().getFin()).append("\n");
        receipt.append("Route: ").append(booking.getRouteName()).append("\n");
        receipt.append("Date: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd").format(booking.getTravelDate())).append("\n");
        receipt.append("Time: ").append(booking.getInterval()).append("\n");
        receipt.append("Seat: ").append(booking.getBusNo()).append("\n");
        receipt.append("\n------------------------------------------\n");
        receipt.append("Total Price: ").append(String.format("%.2f AZN", booking.getPrice())).append("\n");
        receipt.append("==========================================\n");

        receiptTextArea.setText(receipt.toString());
    }

    @FXML
    private void handlePrint() {
        // Implement print functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print");
        alert.setContentText("Receipt sent to printer!");
        alert.showAndWait();
    }

    @FXML
    private void handleSave() {
        try {
            java.io.BufferedWriter writer = new java.io.BufferedWriter(
                    new java.io.FileWriter("receipt_" + System.currentTimeMillis() + ".txt"));
            writer.write(receiptTextArea.getText());
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Receipt saved to file!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to save receipt: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) receiptTextArea.getScene().getWindow();
        stage.close();
    }
}