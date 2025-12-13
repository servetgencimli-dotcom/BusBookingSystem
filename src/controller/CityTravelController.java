package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.util.Date;

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
        // Vaxt intervalları
        try {
            if (BusBookingSystem.cityIntervals != null && BusBookingSystem.cityIntervals.length > 0) {
                ObservableList<String> intervals = FXCollections.observableArrayList(
                        BusBookingSystem.cityIntervals
                );
                intervalCombo.setItems(intervals);
            } else {
                // Default intervallar
                intervalCombo.setItems(FXCollections.observableArrayList(
                        "06:00-07:00", "10:00-11:00", "14:00-15:00", "18:00-19:00", "22:00-23:00"
                ));
            }
        } catch (Exception e) {
            intervalCombo.setItems(FXCollections.observableArrayList(
                    "06:00-07:00", "10:00-11:00", "14:00-15:00", "18:00-19:00", "22:00-23:00"
            ));
        }

        genderCombo.setItems(FXCollections.observableArrayList("K", "Q", "U"));

        // Checkbox dəyişəndə
        useCityCardCheckbox.setOnAction(e -> checkCityCardAvailability());
    }

    private void checkCityCardAvailability() {
        if (!useCityCardCheckbox.isSelected()) {
            return;
        }

        String cityName = cityNameField.getText().trim();

        if (cityName.isEmpty()) {
            messageLabel.setText("⚠️ Əvvəlcə şəhər adı daxil edin!");
            messageLabel.setStyle("-fx-text-fill: #f59e0b;");
            useCityCardCheckbox.setSelected(false);
            return;
        }

        if (currentUser == null || currentUser.cityCards.isEmpty()) {
            messageLabel.setText("❌ City Card-ınız yoxdur!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            useCityCardCheckbox.setSelected(false);
            return;
        }

        // City Card-ın istifadə oluna biləcəyini yoxla
        CityCard usable = null;
        for (CityCard c : currentUser.cityCards) {
            if (c.isUsableFor(cityName) && c.monthsLeft > 0) {
                usable = c;
                break;
            }
        }

        if (usable != null) {
            messageLabel.setText("✓ City Card istifadə olunacaq! Səyahət pulsuzdur.");
            messageLabel.setStyle("-fx-text-fill: #10b981;");
            priceLabel.setText("0.00 AZN");
        } else {
            messageLabel.setText("❌ Bu şəhər üçün aktiv City Card yoxdur!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            useCityCardCheckbox.setSelected(false);
        }
    }

    @FXML
    private void handleBooking() {
        try {
            // 1. Məlumatları əldə et
            String cityName = cityNameField.getText().trim();
            String pName = passengerNameField.getText().trim();
            String pFin = passengerFinField.getText().trim();
            String pAgeStr = passengerAgeField.getText().trim();
            String pGender = genderCombo.getValue();
            String luggageStr = luggageField.getText().trim();
            String interval = intervalCombo.getValue();

            // 2. Validasiya
            if (cityName.isEmpty()) {
                messageLabel.setText("❌ Şəhər adı daxil edin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (pName.isEmpty()) {
                messageLabel.setText("❌ Sərnişin adı daxil edin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (pFin.isEmpty()) {
                messageLabel.setText("❌ FIN kod daxil edin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (pAgeStr.isEmpty()) {
                messageLabel.setText("❌ Yaş daxil edin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (pGender == null) {
                messageLabel.setText("❌ Cins seçin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (luggageStr.isEmpty()) {
                messageLabel.setText("❌ Baqaj çəkisi daxil edin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (interval == null) {
                messageLabel.setText("❌ Vaxt intervalı seçin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            // 3. Rəqəmləri parse et
            int pAge = Integer.parseInt(pAgeStr);
            double luggage = Double.parseDouble(luggageStr);

            // 4. City Card yoxlaması
            CityCard usable = null;
            if (useCityCardCheckbox.isSelected() && currentUser != null) {
                for (CityCard c : currentUser.cityCards) {
                    if (c.isUsableFor(cityName) && c.monthsLeft > 0) {
                        usable = c;
                        break;
                    }
                }
            }

            // 5. Qiymət hesabla
            double price = 0.0;
            String paymentMessage = "";

            if (usable != null) {
                usable.useOneMonth();
                price = 0.0;
                paymentMessage = "✅ City Card istifadə edildi! Səyahət pulsuzdur.\n" +
                        "Qalan ay sayı: " + usable.monthsLeft;
                messageLabel.setText("✓ City Card istifadə edildi!");
                messageLabel.setStyle("-fx-text-fill: #10b981;");
            } else {
                if (BusBookingSystem.payment != null) {
                    price = BusBookingSystem.payment.calculatePayment(10, true, luggage);
                    BusBookingSystem.payment.processPayment(new java.util.Scanner(System.in), price, currentUser);
                } else {
                    // Default qiymət: 10km base + baqaj
                    price = 10 * 0.5 + luggage * 2;
                }
                paymentMessage = "💰 Ödəniş: " + String.format("%.2f", price) + " AZN";
            }

            // 6. Passenger və Booking yarat
            Passenger p = new Passenger(pName, pAge, pGender, luggage, pFin);
            Booking b = new Booking(p, 0, new Date(), price,
                    "City Travel - " + cityName, interval);

            if (BusBookingSystem.bookingManager != null) {
                BusBookingSystem.bookingManager.addBooking(b);
            }

            // 7. Uğur mesajı
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✅ Uğurlu Rezervasiya");
            alert.setHeaderText("City Travel Bileti Hazırdır!");
            alert.setContentText(
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "📋 Rezervasiya Detalları:\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "🏙️ Şəhər: " + cityName + "\n" +
                            "👤 Sərnişin: " + pName + "\n" +
                            "🪪 FIN: " + pFin + "\n" +
                            "👥 Yaş: " + pAge + " | Cins: " + pGender + "\n" +
                            "⏰ Vaxt: " + interval + "\n" +
                            "🧳 Baqaj: " + luggage + " kg\n" +
                            paymentMessage + "\n" +
                            "\n━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "Xoş səyahət! 🚌"
            );
            alert.showAndWait();

            handleCancel();

        } catch (NumberFormatException e) {
            messageLabel.setText("❌ Yaş və baqaj düzgün rəqəm olmalıdır!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            e.printStackTrace();
        } catch (Exception e) {
            messageLabel.setText("❌ Xəta: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml","Back");
        Stage stage = (Stage) cityNameField.getScene().getWindow();
        stage.close();
    }
}