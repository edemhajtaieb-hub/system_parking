package client;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    // Constructeur : configuration de la fenêtre principale
    public MainMenu() {
        setTitle("Smart Parking v2 - Menu Principal");  // titre de la fenêtre
        setSize(400, 250);                              // taille initiale
        setLocationRelativeTo(null);                    // centrer la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);       // fermeture de l'application

        initUI();  // création de l'interface graphique
    }

    /** Création de l'interface utilisateur */
    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS)); // disposition verticale
        main.setBackground(new Color(240, 240, 240));          // couleur de fond
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // marges internes

        // Titre de l'application
        JLabel title = new JLabel("SMART PARKING v2");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // centrer horizontalement

        // Bouton pour accéder à l’interface admin
        JButton adminBtn = new JButton("🔵 Accéder à l’Admin");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 14));
        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bouton pour accéder à l’interface client (réservation)
        JButton clientBtn = new JButton("🟢 Accéder à la Réservation");
        clientBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clientBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajouter les composants au panel principal avec des espacements
        main.add(title);
        main.add(Box.createVerticalStrut(20));  // espace vertical
        main.add(adminBtn);
        main.add(Box.createVerticalStrut(15));
        main.add(clientBtn);

        add(main); // ajouter le panel principal à la fenêtre

        // -----------------------
        // ACTIONS / LISTENERS
        // -----------------------
        adminBtn.addActionListener(e -> openAdmin());   // ouvre l’interface admin
        clientBtn.addActionListener(e -> openClient()); // ouvre l’interface client
    }

    /** Ouvre la fenêtre AdminPanelV2 */
    private void openAdmin() {
        new AdminPanelV2().setVisible(true);
    }

    /** Ouvre la fenêtre ClientSwing */
    private void openClient() {
        new ClientSwing().setVisible(true);
    }

    /** Point d'entrée principal */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
