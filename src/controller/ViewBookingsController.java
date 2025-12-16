package controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ViewBookingsController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private TableView<BookingData> bookingsTable;
    @FXML private TableColumn<BookingData, String> passengerCol;
    @FXML private TableColumn<BookingData, String> routeCol;
    @FXML private TableColumn<BookingData, String> dateCol;
    @FXML private TableColumn<BookingData, String> intervalCol;
    @FXML private TableColumn<BookingData, Double> priceCol;
    @FXML private Button backBtn;

    private User currentUser;
    private boolean showAllBookings = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passengerCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        intervalCol.setCellValueFactory(new PropertyValueFactory<>("interval"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void setUserBookings(User user) {
        this.currentUser = user;
        this.showAllBookings = false;
        titleLabel.setText("MY BOOKINGS");
        loadBookings();
    }

    public void setAllBookings() {
        this.showAllBookings = true;
        titleLabel.setText("ALL BOOKINGS");
        loadBookings();
    }

    private void loadBookings() {
        ObservableList<BookingData> data = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Booking b : BusBookingSystem.bookingManager.bookings) {
            if (showAllBookings || b.getPassenger().getFin().equals(currentUser.fin)) {
                data.add(new BookingData(
                        b.getPassenger().getName(),
                        b.getRouteName(),
                        sdf.format(b.getTravelDate()),
                        b.getInterval(),
                        b.getPrice()
                ));
            }
        }

        bookingsTable.setItems(data);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            String fxml = showAllBookings ? "/project/fxml/AdminMenu.fxml" : "/project/fxml/UserMenu.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class BookingData {
        private String passengerName;
        private String route;
        private String date;
        private String interval;
        private Double price;

        public BookingData(String passengerName, String route, String date, String interval, Double price) {
            this.passengerName = passengerName;
            this.route = route;
            this.date = date;
            this.interval = interval;
            this.price = price;
        }

        public String getPassengerName() { return passengerName; }
        public String getRoute() { return route; }
        public String getDate() { return date; }
        public String getInterval() { return interval; }
        public Double getPrice() { return price; }
    }
}