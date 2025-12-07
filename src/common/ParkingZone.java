package common;

import java.io.Serializable;

public class ParkingZone implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;

    public ParkingZone() {}
    public ParkingZone(String name, String desc) { this.name = name; this.description = desc; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    @Override
    public String toString() { return name + (description==null? "" : " ("+description+")"); }
}
