package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class CityCardController {
    @FXML private RadioButton globalCardRadio, customCardRadio;
    @FXML private Spinner<Integer> cityCountSpinner, monthsSpinner;
    @FXML private VBox customCitiesBox, cityFieldsContainer;
    @FXML private Label totalLabel, messageLabel;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }


    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> cityFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        cityCountSpinner.setValueFactory(cityFactory);

        SpinnerValueFactory<Integer> monthFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        monthsSpinner.setValueFactory(monthFactory);

        globalCardRadio.setOnAction(e -> {
            customCitiesBox.setDisable(true);
            calculateTotal();
        });

        customCardRadio.setOnAction(e -> {
            customCitiesBox.setDisable(false);
            calculateTotal();
        });

        cityCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateCityFields();
            calculateTotal();
        });

        monthsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    @FXML
    private void updateCityFields() {
        int count = cityCountSpinner.getValue();
        cityFieldsContainer.getChildren().clear();

        for (int i = 0; i < count; i++) {
            TextField cityField = new TextField();
            cityField.setPromptText("City " + (i + 1) + " name");
            cityFieldsContainer.getChildren().add(cityField);
        }
    }

    @FXML
    private void calculateTotal() {
        int months = monthsSpinner.getValue();
        double total;

        if (globalCardRadio.isSelected()) {
            total = Payment.calculateCityCardTotal(0, months, true);
        } else {
            int cityCount = cityCountSpinner.getValue();
            total = Payment.calculateCityCardTotal(cityCount, months, false);
        }

        totalLabel.setText(String.format("%.2f AZN", total));
    }

    @FXML
    private void handlePurchase() {
        try {
            int months = monthsSpinner.getValue();
            double total;
            java.util.List<String> cities = new java.util.ArrayList<>();

            if (globalCardRadio.isSelected()) {
                cities.add("GLOBAL");
                total = Payment.calculateCityCardTotal(0, months, true);
            } else {
                for (javafx.scene.Node node : cityFieldsContainer.getChildren()) {
                    if (node instanceof TextField) {
                        String city = ((TextField) node).getText().trim();
                        if (!city.isEmpty()) {
                            cities.add(city);
                        }
                    }
                }

                if (cities.isEmpty()) {
                    messageLabel.setText("Please enter at least one city!");
                    return;
                }

                total = Payment.calculateCityCardTotal(cities.size(), months, false);
            }

            BusBookingSystem.payment.processPayment(new java.util.Scanner(System.in), total, currentUser);

            CityCard cc = new CityCard(cities, months);
            currentUser.cityCards.add(cc);
            CityCardDAO.saveCityCard(currentUser.fin, cc);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("City card purchased successfully! Total: " + total + " AZN");
            alert.showAndWait();

            handleCancel();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml","Back");
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }
}
