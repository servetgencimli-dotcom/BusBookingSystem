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

    public void displayBooking() {
        System.out.println("Bus_booking_project.Booking -> " + passenger.getName() + " | Route: " + routeName + " | Bus_booking_project.Bus No: " + busNo +
                " | Date: " + travelDate + " | Interval: " + interval + " | Price: " + price + " AZN | Gender: " + passenger.getGender());
    }
}
