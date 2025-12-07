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
    private IReservationService service;
    private DefaultListModel<ParkingSpot> listModel = new DefaultListModel<>();
    private JList<ParkingSpot> list = new JList<>(listModel);
    private JComboBox<String> regionCombo = new JComboBox<>();
    private JTextField nameField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JTextField plateField = new JTextField(10);
    private JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
    private JLabel amountLabel = new JLabel("Montant : 0.0");
    private JButton payBtn = new JButton("Payer");
    private String reservationId = null;
    private String clientKey = null;

    public ClientSwing() {
        setTitle("Smart Parking v2 - Client");
        setSize(750, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        connectToServer();
        initUI();
        loadZonesAndSpots();
        welcomeMessage();
    }

    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            service = (IReservationService) reg.lookup("ParkingService");
        } catch (Exception e) {
            showError("Erreur connexion serveur: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initUI() {
        Color bg = new Color(245,245,245);
        Color blue = new Color(66,135,245);
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(bg); main.setBorder(new EmptyBorder(10,10,10,10));

        // left
        JPanel left = new JPanel(new BorderLayout()); left.setBackground(bg);
        JLabel lbl = new JLabel("Places disponibles :"); lbl.setFont(new Font("Arial", Font.BOLD, 15));
        JPanel topLeft = new JPanel(); topLeft.setBackground(bg); topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.add(new JLabel("Choisir zone :")); topLeft.add(regionCombo);
        topLeft.add(Box.createVerticalStrut(8)); topLeft.add(lbl);
        left.add(topLeft, BorderLayout.NORTH);
        list.setBorder(new LineBorder(blue,1)); left.add(new JScrollPane(list), BorderLayout.CENTER);
        main.add(left, BorderLayout.WEST);

        // right
        JPanel right = new JPanel(); right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS)); right.setBackground(bg);
        right.add(new JLabel("Nom client :")); right.add(nameField);
        right.add(new JLabel("Téléphone (8 chiffres) :")); right.add(phoneField);
        right.add(new JLabel("Plaque voiture :")); right.add(plateField);
        right.add(new JLabel("Heures :")); right.add(hoursSpinner);
        right.add(Box.createVerticalStrut(8));
        JButton reserveBtn = new JButton("Réserver"); reserveBtn.setBackground(new Color(52,152,219)); reserveBtn.setForeground(Color.WHITE);
        payBtn.setBackground(new Color(46,204,113)); payBtn.setForeground(Color.WHITE); payBtn.setEnabled(false);
        right.add(reserveBtn); right.add(Box.createVerticalStrut(8)); right.add(amountLabel); right.add(Box.createVerticalStrut(8)); right.add(payBtn);
        main.add(right, BorderLayout.CENTER);

        add(main);

        // actions
        reserveBtn.addActionListener(e -> reserveSpot());
        payBtn.addActionListener(e -> payReservation());
        regionCombo.addActionListener(e -> loadSpots());
    }

    private void welcomeMessage() {
        JOptionPane.showMessageDialog(this, "🚗 Bienvenue dans Smart Parking v2 !\nRéservez & payez en quelques clics.", "Bienvenue", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadZonesAndSpots() {
        try {
            regionCombo.removeAllItems();
            List<ParkingZone> zones = service.listZones();
            regionCombo.addItem(""); // toutes
            for (ParkingZone z : zones) regionCombo.addItem(z.getName());
            loadSpots();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadSpots() {
        try {
            listModel.clear();
            String region = (String) regionCombo.getSelectedItem();
            List<ParkingSpot> spots = service.listAvailableSpots(region==null? "": region);
            for (ParkingSpot p : spots) listModel.addElement(p);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void reserveSpot() {
        // validations
        String nom = nameField.getText().trim();
        if (nom.isEmpty()) { showError("Le nom est obligatoire !"); return; }
        if (!nom.matches("[A-Z][a-zA-Z ]*")) { showError("Le nom doit commencer par une majuscule !"); return; }

        String tel = phoneField.getText().trim();
        if (tel.isEmpty()) { showError("Le téléphone est obligatoire !"); return; }
        if (!tel.matches("\\d{8}")) { showError("Le téléphone doit contenir 8 chiffres !"); return; }

        String plate = plateField.getText().trim();
        if (plate.isEmpty()) { showError("Plaque voiture obligatoire !"); return; }

        try {
            ParkingSpot spot = list.getSelectedValue();
            if (spot == null) { showError("Choisissez une place."); return; }
            ClientInfo client = new ClientInfo(nom, tel);
            Vehicle veh = new Vehicle(plate, "Modèle", "Couleur");
            int hours = (int) hoursSpinner.getValue();
            Reservation r = service.reserveSpot(client, veh, spot.getId(), hours);
            reservationId = r.getId();
            amountLabel.setText("Montant : " + r.getAmount() + " DT");
            payBtn.setEnabled(true);
            loadSpots();

            // register listener callback (use phone as client key)
            if (clientKey == null) {
                clientKey = tel;
                try {
                    ClientNotificationListener listener = new ClientNotificationListener();
                    INotificationListener stub = (INotificationListener) UnicastRemoteObject.exportObject(listener, 0);
                    service.registerListener(clientKey, stub);
                } catch (Exception ex) {
                    System.err.println("Impossible register callback: " + ex.getMessage());
                }
            }

            // success dialog
            JLabel success = new JLabel("<html><font color='green'>✅ Réservation OK !<br>Réf: " + r.getId() + "<br>Place: " + r.getSpot().getLabel() + "</font></html>");
            JOptionPane.showMessageDialog(this, success, "Réservé", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void payReservation() {
        try {
            if (reservationId == null) { showError("Aucune réservation à payer."); return; }
            Reservation r = service.getReservation(reservationId);
            Payment p = new Payment("carte", r.getAmount());
            service.payReservation(reservationId, p);
            payBtn.setEnabled(false);
            loadSpots();

            JLabel success = new JLabel("<html><font color='green'>✅ Paiement effectué !<br>Montant: " + r.getAmount() + " DT<br>Place: " + r.getSpot().getLabel() + "</font></html>");
            JOptionPane.showMessageDialog(this, success, "Paiement", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JLabel errorLabel = new JLabel("<html><font color='red'>❌ " + message + "</font></html>");
        JOptionPane.showMessageDialog(this, errorLabel, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // nested class: listener implementation => will receive server notifications
    private class ClientNotificationListener implements INotificationListener {
        @Override
        public void onNotification(Notification notification) {
            // Swing operations must run on EDT
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(ClientSwing.this,
                        "<html><b>" + notification.getTitle() + "</b><br>" + notification.getMessage() + "</html>",
                        "Notification serveur", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
}
