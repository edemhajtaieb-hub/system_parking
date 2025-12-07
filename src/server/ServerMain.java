package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principale pour lancer le serveur RMI Smart Parking
 */
public class ServerMain {

    public static void main(String[] args) {
        try {
            // Création du registre RMI sur le port 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry lancé...");

            // Instanciation du service de réservation
            ReservationServiceImpl service = new ReservationServiceImpl();

            // Liaison du service au registre RMI avec le nom "ParkingService"
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("ParkingService", service);
            System.out.println("Serveur prêt !");

            // Instanciation et liaison du service Admin au registre avec le nom "AdminService"
            AdminServiceImpl admin = new AdminServiceImpl(service);
            reg.rebind("AdminService", admin);
            System.out.println("Admin service prêt !");

        } catch (Exception e) {
            // Affiche les erreurs éventuelles
            e.printStackTrace();
        }
    }

}
