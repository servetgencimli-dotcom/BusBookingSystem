package project;

import java.util.Date;

public class Booking {
    private Passenger passenger;
    private int busNo;
    private Date travelDate;
    private double price;
    private String routeName;
    private String interval;

    public Booking(Passenger passenger, int busNo, Date travelDate, double price, String routeName, String interval) {
        this.passenger = passenger;
        this.busNo = busNo;
        this.travelDate = travelDate;
        this.price = price;
        this.routeName = routeName;
        this.interval = interval;
    }

    // Getters
    public Passenger getPassenger() {
        return passenger;
    }

    public int getBusNo() {
        return busNo;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public double getPrice() {
        return price;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getInterval() {
        return interval;
    }

    // Setters
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void setBusNo(int busNo) {
        this.busNo = busNo;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "Booking{passenger=" + passenger.getName() +
                ", busNo=" + busNo +
                ", travelDate=" + travelDate +
                ", price=" + price +
                ", routeName='" + routeName + "'" +
                ", interval='" + interval + "'}";
    }
}