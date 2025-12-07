package common;

import java.io.Serializable;

/**
 * Classe représentant un véhicule associé à une réservation de parking.
 * Contient les informations essentielles du véhicule : plaque, modèle, couleur.
 */
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Numéro de plaque du véhicule */
    private String plateNumber;

    /** Modèle du véhicule */
    private String model;

    /** Couleur du véhicule */
    private String color;

    /** Constructeur par défaut (nécessaire pour la sérialisation RMI) */
    public Vehicle() {}

    /**
     * Constructeur principal
     * @param plate numéro de plaque
     * @param model modèle du véhicule
     * @param color couleur du véhicule
     */
    public Vehicle(String plate, String model, String color) {
        this.plateNumber = plate;
        this.model = model;
        this.color = color;
    }

    // ======= Getters =======
    public String getPlateNumber() { return plateNumber; }

    public String getModel() { return model; }

    public String getColor() { return color; }

    @Override
    public String toString() {
        // Affiche la plaque et éventuellement le modèle si disponible
        return plateNumber + (model == null ? "" : " - " + model);
    }
}
