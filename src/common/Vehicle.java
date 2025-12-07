package common;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String plateNumber;
    private String model;
    private String color;

    public Vehicle() {}
    public Vehicle(String plate, String model, String color) {
        this.plateNumber = plate; this.model = model; this.color = color;
    }
    public String getPlateNumber() { return plateNumber; }
    public String getModel() { return model; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return plateNumber + (model==null? "" : " - " + model);
    }
}
