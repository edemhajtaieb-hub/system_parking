package client;

import common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientSwing extends JFrame {

    // Référence vers le service RMI de réservation
    private IReservationService service;

    // Modèle pour afficher les places disponibles
    private DefaultListModel<ParkingSpot> listModel = new DefaultListModel<>();
    private JList<ParkingSpot> list = new JList<>(listModel);

    // Champs d’entrée utilisateur
    private JComboBox<String> regionCombo = new JComboBox<>();
    private JTextField nameField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JTextField plateField = new JTextField(10);
    private JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));

    // Affichage du montant et bouton Paiement
    private JLabel amountLabel = new JLabel("Montant : 0.0");
    private JButton payBtn = new JButton("Payer");

    // Identifiants internes de réservation
    private String reservationId = null;
    private String clientKey = null;

    public ClientSwing() {
        setTitle("Smart Parking v2 - Client");
        setSize(750, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToServer();       // Connexion au serveur RMI
        initUI();                // Construction de l'interface graphique
        loadZonesAndSpots();     // Charger zones + places disponibles
        welcomeMessage();        // Message de bienvenue
    }

    /** Connexion au serveur RMI */
    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            service = (IReservationService) reg.lookup("ParkingService");
        } catch (Exception e) {
            showError("Erreur connexion serveur: " + e.getMessage());
            System.exit(1);
        }
    }

    /** Création des éléments graphiques */
    private void initUI() {
        Color bg = new Color(245,245,245);
        Color blue = new Color(66,135,245);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bg);
        main.setBorder(new EmptyBorder(10,10,10,10));

        // -----------------------
        // COLONNE GAUCHE : LISTE DES PLACES
        // -----------------------
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(bg);

        JLabel lbl = new JLabel("Places disponibles :");
        lbl.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel topLeft = new JPanel();
        topLeft.setBackground(bg);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));

        topLeft.add(new JLabel("Choisir zone :"));
        topLeft.add(regionCombo);
        topLeft.add(Box.createVerticalStrut(8));
        topLeft.add(lbl);

        left.add(topLeft, BorderLayout.NORTH);

        // Liste affichant les places disponibles
        list.setBorder(new LineBorder(blue,1));
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        main.add(left, BorderLayout.WEST);

        // -----------------------
        // COLONNE DROITE : FORMULAIRE CLIENT
        // -----------------------
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(bg);

        right.add(new JLabel("Nom client :"));
        right.add(nameField);

        right.add(new JLabel("Téléphone (8 chiffres) :"));
        right.add(phoneField);

        right.add(new JLabel("Plaque voiture :"));
        right.add(plateField);

        right.add(new JLabel("Heures :"));
        right.add(hoursSpinner);
        right.add(Box.createVerticalStrut(8));

        // Bouton réservation
        JButton reserveBtn = new JButton("Réserver");
        reserveBtn.setBackground(new Color(52,152,219));
        reserveBtn.setForeground(Color.WHITE);

        // Bouton paiement
        payBtn.setBackground(new Color(46,204,113));
        payBtn.setForeground(Color.WHITE);
        payBtn.setEnabled(false);

        right.add(reserveBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(amountLabel);
        right.add(Box.createVerticalStrut(8));
        right.add(payBtn);

        main.add(right, BorderLayout.CENTER);
        add(main);

        // -----------------------
        // ACTIONS / LISTENERS
        // -----------------------
        reserveBtn.addActionListener(e -> reserveSpot());
        payBtn.addActionListener(e -> payReservation());
        regionCombo.addActionListener(e -> loadSpots());
    }

    /** Petit message de bienvenue */
    private void welcomeMessage() {
        JOptionPane.showMessageDialog(this,
                "Bienvenue dans Smart Parking v2 !\nRéservez & payez en quelques clics.",
                "Bienvenue",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Charger les zones et ensuite les places associées */
    private void loadZonesAndSpots() {
        try {
            regionCombo.removeAllItems();
            List<ParkingZone> zones = service.listZones();

            regionCombo.addItem(""); // option = toutes les zones
            for (ParkingZone z : zones)
                regionCombo.addItem(z.getName());

            loadSpots();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Charger les places disponibles selon la zone sélectionnée */
    private void loadSpots() {
        try {
            listModel.clear();
            String region = (String) regionCombo.getSelectedItem();
            List<ParkingSpot> spots = service.listAvailableSpots(region == null ? "" : region);

            for (ParkingSpot p : spots)
                listModel.addElement(p);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Réserver une place */
    private void reserveSpot() {

        // -----------------------
        // VALIDATIONS FORMULAIRES
        // -----------------------
        String nom = nameField.getText().trim();
        if (nom.isEmpty()) { showError("Le nom est obligatoire !"); return; }
        if (!nom.matches("[A-Z][a-zA-Z ]*")) {
            showError("Le nom doit commencer par une majuscule !");
            return;
        }

        String tel = phoneField.getText().trim();
        if (tel.isEmpty()) { showError("Le téléphone est obligatoire !"); return; }
        if (!tel.matches("\\d{8}")) { showError("Le téléphone doit contenir 8 chiffres !"); return; }

        String plate = plateField.getText().trim();
        if (plate.isEmpty()) { showError("Plaque voiture obligatoire !"); return; }

        try {
            // Vérifier qu'une place est sélectionnée
            ParkingSpot spot = list.getSelectedValue();
            if (spot == null) {
                showError("Choisissez une place.");
                return;
            }

            // Création des objets métier
            ClientInfo client = new ClientInfo(nom, tel);
            Vehicle veh = new Vehicle(plate, "Modèle", "Couleur");
            int hours = (int) hoursSpinner.getValue();

            // Appel RMI => réservation
            Reservation r = service.reserveSpot(client, veh, spot.getId(), hours);
            reservationId = r.getId();

            amountLabel.setText("Montant : " + r.getAmount() + " DT");
            payBtn.setEnabled(true);

            loadSpots(); // rafraîchir les places

            // -----------------------
            // ENREGISTRER LE LISTENER DE NOTIFICATION
            // -----------------------
            if (clientKey == null) {
                clientKey = tel;

                try {
                    ClientNotificationListener listener = new ClientNotificationListener();

                    INotificationListener stub =
                            (INotificationListener) UnicastRemoteObject.exportObject(listener, 0);

                    service.registerListener(clientKey, stub);

                } catch (Exception ex) {
                    System.err.println("Impossible register callback: " + ex.getMessage());
                }
            }

            // Message confirmation
            JLabel success = new JLabel("<html><font color='green'>Réservation OK !<br>Réf: "
                    + r.getId() + "<br>Place: " + r.getSpot().getLabel() + "</font></html>");

            JOptionPane.showMessageDialog(this, success, "Réservé", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Effectuer le paiement */
    private void payReservation() {
        try {
            if (reservationId == null) {
                showError("Aucune réservation à payer.");
                return;
            }

            Reservation r = service.getReservation(reservationId);
            Payment p = new Payment("carte", r.getAmount());

            // Paiement côté serveur
            service.payReservation(reservationId, p);

            payBtn.setEnabled(false);
            loadSpots();

            JLabel success = new JLabel("<html><font color='green'> Paiement effectué !<br>Montant: "
                    + r.getAmount() + " DT<br>Place: " + r.getSpot().getLabel() + "</font></html>");

            JOptionPane.showMessageDialog(this, success, "Paiement", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Affichage d'une erreur dans une boîte de dialogue */
    private void showError(String message) {
        JLabel errorLabel = new JLabel("<html><font color='red'> " + message + "</font></html>");
        JOptionPane.showMessageDialog(this, errorLabel, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /** Classe interne : permettra de recevoir les notifications du serveur */
    private class ClientNotificationListener implements INotificationListener {
        @Override
        public void onNotification(Notification notification) {

            // Important: interaction graphique sur l’Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {

                JOptionPane.showMessageDialog(ClientSwing.this,
                        "<html><b>" + notification.getTitle() + "</b><br>" + notification.getMessage() + "</html>",
                        "Notification serveur",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
}
