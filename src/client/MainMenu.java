package client;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Smart Parking v2 - Menu Principal");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(240, 240, 240));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("SMART PARKING v2");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton adminBtn = new JButton("🔵 Accéder à l’Admin");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 14));
        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton clientBtn = new JButton("🟢 Accéder à la Réservation");
        clientBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clientBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(title);
        main.add(Box.createVerticalStrut(20));
        main.add(adminBtn);
        main.add(Box.createVerticalStrut(15));
        main.add(clientBtn);

        add(main);

        // actions
        adminBtn.addActionListener(e -> openAdmin());
        clientBtn.addActionListener(e -> openClient());
    }

    private void openAdmin() {
        new AdminPanelV2().setVisible(true);
    }

    private void openClient() {
        new ClientSwing().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
