package project;

public class Passenger {
    private String name;
    private int age;
    private String gender;
    private double luggage;
    private String fin;

    public Passenger(String name, int age, String gender, double luggage, String fin) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.luggage = luggage;
        this.fin = fin;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public double getLuggage() {
        return luggage;
    }

    public String getFin() {
        return fin;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLuggage(double luggage) {
        this.luggage = luggage;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    @Override
    public String toString() {
        return "Passenger{name='" + name + "', age=" + age + ", gender='" + gender +
                "', luggage=" + luggage + ", fin='" + fin + "'}";
    }
}