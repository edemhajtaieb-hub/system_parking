package common;

import java.io.Serializable;

/**
 * Classe représentant les informations d'un client.
 * Implémente Serializable pour pouvoir être envoyée via RMI ou sauvegardée.
 */
public class ClientInfo implements Serializable {

    private static final long serialVersionUID = 1L; // identifiant pour la sérialisation

    private String name;   // nom du client
    private String phone;  // numéro de téléphone du client

    /** Constructeur par défaut (nécessaire pour certaines opérations RMI/sérialisation) */
    public ClientInfo() {}

    /** Constructeur avec nom et téléphone */
    public ClientInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    /** Getter pour le nom */
    public String getName() {
        return name;
    }

    /** Getter pour le téléphone */
    public String getPhone() {
        return phone;
    }

    /**
     * Représentation textuelle du client
     * Affiche le nom et le téléphone entre parenthèses si disponible
     */
    @Override
    public String toString() {
        return name + (phone == null ? "" : " (" + phone + ")");
    }
}
