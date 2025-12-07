package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingSpot implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String label;
    private boolean reserved;
    private String region;
    private final List<SpotHistory> history = new ArrayList<>();

    public ParkingSpot() {}
    public ParkingSpot(int id, String label, String region) {
        this.id = id; this.label = label; this.region = region; this.reserved = false;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    // history methods
    public synchronized void addHistory(SpotHistory item) {
        history.add(0, item); // add newest first
    }
    public synchronized List<SpotHistory> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    @Override public String toString() {
        return label + " - " + region + (reserved? " (RESERVÉE)": " (LIBRE)");
    }
}
