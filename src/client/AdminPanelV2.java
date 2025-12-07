package client;

import common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class AdminPanelV2 extends JFrame {

    private IAdminService adminService;

    // left
    private DefaultTableModel zonesModel;
    private JTable zonesTable;

    // middle
    private DefaultTableModel spotsModel;
    private JTable spotsTable;

    // right
    private DefaultTableModel historyModel;
    private JTable historyTable;

    public AdminPanelV2() {
        setTitle("Smart Parking - Admin V2");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        connectToServer();
        initUI();
        loadZones();
    }

    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            adminService = (IAdminService) reg.lookup("AdminService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur connexion serveur: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(10,10,10,10));
        getContentPane().add(main);

        // left: zones
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(260, 0));
        zonesModel = new DefaultTableModel(new String[]{"Nom Zone", "Total", "Disponibles"}, 0);
        zonesTable = new JTable(zonesModel);
        left.add(new JScrollPane(zonesTable), BorderLayout.CENTER);

        JPanel zoneActions = new JPanel();
        JButton addZoneBtn = new JButton("Ajouter Zone");
        JButton delZoneBtn = new JButton("Supprimer Zone");
        zoneActions.add(addZoneBtn); zoneActions.add(delZoneBtn);
        left.add(zoneActions, BorderLayout.SOUTH);

        // middle: spots
        JPanel middle = new JPanel(new BorderLayout());
        spotsModel = new DefaultTableModel(new String[]{"ID","Label","Etat","Zone"}, 0);
        spotsTable = new JTable(spotsModel);
        middle.add(new JScrollPane(spotsTable), BorderLayout.CENTER);

        JPanel spotActions = new JPanel();
        JButton addSpotBtn = new JButton("Ajouter Place");
        JButton delSpotBtn = new JButton("Supprimer Place");
        JButton freeSpotBtn = new JButton("Libérer Place");
        spotActions.add(addSpotBtn); spotActions.add(delSpotBtn); spotActions.add(freeSpotBtn);
        middle.add(spotActions, BorderLayout.SOUTH);

        // right: history
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(360,0));
        historyModel = new DefaultTableModel(new String[]{"Date","Type","Details","Montant"}, 0);
        historyTable = new JTable(historyModel);
        right.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel histTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        histTop.add(new JLabel("Historique place sélectionnée"));
        right.add(histTop, BorderLayout.NORTH);

        // assemble
        main.add(left, BorderLayout.WEST);
        main.add(middle, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        // actions
        addZoneBtn.addActionListener(e -> onAddZone());
        delZoneBtn.addActionListener(e -> onDeleteZone());
        zonesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onZoneSelected();
        });

        addSpotBtn.addActionListener(e -> onAddSpot());
        delSpotBtn.addActionListener(e -> onDeleteSpot());
        freeSpotBtn.addActionListener(e -> onFreeSpot());
        spotsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSpotSelected();
        });
    }

    private void loadZones() {
        try {
            zonesModel.setRowCount(0);
            List<ParkingZone> zones = adminService.listZones();
            for (ParkingZone z : zones) {
                // compute total & available quickly by querying spots
                List<ParkingSpot> spots = adminService.listSpotsByZone(z.getName());
                long total = spots.size();
                long free = spots.stream().filter(s -> !s.isReserved()).count();
                zonesModel.addRow(new Object[]{z.getName(), total, free});
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onZoneSelected() {
        int row = zonesTable.getSelectedRow();
        if (row == -1) return;
        String zoneName = (String) zonesModel.getValueAt(row, 0);
        loadSpots(zoneName);
    }

    private void loadSpots(String zoneName) {
        try {
            spotsModel.setRowCount(0);
            List<ParkingSpot> spots = adminService.listSpotsByZone(zoneName);
            for (ParkingSpot p : spots) {
                spotsModel.addRow(new Object[]{p.getId(), p.getLabel(), p.isReserved() ? "Réservée" : "Disponible", p.getRegion()});
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onSpotSelected() {
        int row = spotsTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) spotsModel.getValueAt(row, 0);
        loadHistory(id);
    }

    private void loadHistory(int spotId) {
        try {
            historyModel.setRowCount(0);
            List<SpotHistory> hist = adminService.getSpotHistory(spotId);
            for (SpotHistory h : hist) {
                historyModel.addRow(new Object[]{h.getDate().toString(), h.getType(), h.getDetails(), h.getAmount()});
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onAddZone() {
        String name = JOptionPane.showInputDialog(this, "Nom nouvelle zone:");
        if (name == null || name.trim().isEmpty()) return;
        try {
            boolean ok = adminService.addZone(name.trim());
            if (!ok) showError("Zone existante ou erreur.");
            loadZones();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onDeleteZone() {
        int row = zonesTable.getSelectedRow();
        if (row == -1) { showError("Choisissez une zone."); return; }
        String name = (String) zonesModel.getValueAt(row, 0);
        try {
            boolean ok = adminService.removeZone(name);
            if (!ok) showError("Impossible supprimer zone (peut contenir places) ou erreur.");
            loadZones();
            spotsModel.setRowCount(0);
            historyModel.setRowCount(0);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onAddSpot() {
        int row = zonesTable.getSelectedRow();
        if (row == -1) { showError("Sélectionnez une zone d'abord."); return; }
        String zone = (String) zonesModel.getValueAt(row, 0);
        String label = JOptionPane.showInputDialog(this, "Label nouvelle place (ex: C1):");
        if (label == null || label.trim().isEmpty()) return;
        try {
            ParkingSpot p = adminService.addSpot(label.trim(), zone);
            loadZones();
            loadSpots(zone);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onDeleteSpot() {
        int row = spotsTable.getSelectedRow();
        if (row == -1) { showError("Sélectionnez une place."); return; }
        int id = (int) spotsModel.getValueAt(row, 0);
        try {
            boolean ok = adminService.removeSpot(id);
            if (!ok) showError("Erreur suppression place.");
            loadZones();
            int zRow = zonesTable.getSelectedRow();
            if (zRow != -1) onZoneSelected();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onFreeSpot() {
        int row = spotsTable.getSelectedRow();
        if (row == -1) { showError("Sélectionnez une place."); return; }
        int id = (int) spotsModel.getValueAt(row, 0);
        try {
            boolean ok = adminService.freeSpot(id);
            if (!ok) showError("Erreur libération.");
            loadZones();
            onZoneSelected();
            loadHistory(id);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, "Erreur: " + msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // quick launcher
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanelV2().setVisible(true));
    }
}

