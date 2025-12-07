package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry lancé...");
            ReservationServiceImpl service = new ReservationServiceImpl();
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("ParkingService", service);
            System.out.println("Serveur prêt !");
            AdminServiceImpl admin = new AdminServiceImpl(service);
            reg.rebind("AdminService", admin);
            System.out.println("Admin service prêt !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

