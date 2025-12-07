package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe représentant une place de parking.
 * Contient les informations de base (id, label, zone), son état (réservée ou libre)
 * et l'historique des réservations.
 */
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identifiant unique de la place */
    private int id;

    /** Label ou numéro de la place (ex: A1, B3) */
    private String label;

    /** État de réservation : true si la place est réservée */
    private boolean reserved;

    /** Zone ou région où se situe la place */
    private String region;

    /** Historique des actions sur cette place (réservations, libérations, paiements…) */
    private final List<SpotHistory> history = new ArrayList<>();

    /** Constructeur vide pour sérialisation/RMI */
    public ParkingSpot() {}

    /**
     * Constructeur principal
     * @param id identifiant unique de la place
     * @param label nom ou label de la place
     * @param region zone à laquelle appartient la place
     */
    public ParkingSpot(int id, String label, String region) {
        this.id = id;
        this.label = label;
        this.region = region;
        this.reserved = false; // par défaut, la place est libre
    }

    // ======= Getters et Setters =======

    public int getId() { return id; }

    public String getLabel() { return label; }

    public boolean isReserved() { return reserved; }

    public void setReserved(boolean reserved) { this.reserved = reserved; }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    // ======= Gestion de l'historique =======

    /**
     * Ajoute un élément à l'historique.
     * Les nouvelles actions sont ajoutées en début de liste.
     * @param item l'événement à ajouter
     */
    public synchronized void addHistory(SpotHistory item) {
        history.add(0, item); // ajout en tête de liste pour que le plus récent soit en premier
    }

    /**
     * Retourne l'historique des actions sous forme de liste non modifiable.
     * @return liste de SpotHistory
     */
    public synchronized List<SpotHistory> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    /**
     * Représentation texte de la place de parking, utilisée pour affichage dans les listes
     */
    @Override
    public String toString() {
        return label + " - " + region + (reserved ? " (RESERVÉE)" : " (LIBRE)");
    }
}
