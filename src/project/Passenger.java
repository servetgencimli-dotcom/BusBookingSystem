package project;

public class Passenger {
    private String name, gender, fin;
    private int age;
    private double luggageWeight;

    public Passenger(String name, int age, String gender, double luggageWeight, String fin) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.luggageWeight = luggageWeight;
        this.fin = fin;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public double getLuggageWeight() {
        return luggageWeight;
    }

    public String getFin() {
        return fin;
    }

    public Object getUserFin() {
        return fin;
    }
}
