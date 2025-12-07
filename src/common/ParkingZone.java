package common;

import java.io.Serializable;

/**
 * Classe représentant une zone de parking.
 * Une zone regroupe plusieurs places de parking et peut avoir une description.
 */
public class ParkingZone implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nom de la zone (ex: Zone A, Hopital, Cinéma) */
    private String name;

    /** Description optionnelle de la zone (ex: "Proche entrée principale") */
    private String description;

    /** Constructeur vide pour sérialisation/RMI */
    public ParkingZone() {}

    /**
     * Constructeur principal
     * @param name nom de la zone
     * @param desc description de la zone
     */
    public ParkingZone(String name, String desc) {
        this.name = name;
        this.description = desc;
    }

    // ======= Getters =======
    public String getName() { return name; }

    public String getDescription() { return description; }

    /**
     * Représentation texte de la zone
     * Affiche le nom et la description entre parenthèses si elle existe
     */
    @Override
    public String toString() {
        return name + (description == null ? "" : " (" + description + ")");
    }
}
