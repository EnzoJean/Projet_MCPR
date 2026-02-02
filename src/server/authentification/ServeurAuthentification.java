package server.authentification;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Lance le serveur d'authentification RMI sur le port 1099
 */
public class ServeurAuthentification {

    public static final int PORT = 1099;
    public static final String NOM_SERVICE = "ServiceAuthentification";

    public static void main(String[] args) {
        try {
            System.out.println("==============================================");
            System.out.println("  PART'TOOL - Serveur d'Authentification");
            System.out.println("==============================================\n");

            ServiceAuthentificationImpl service = new ServiceAuthentificationImpl();
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind(NOM_SERVICE, service);

            System.out.println("Serveur démarré");
            System.out.println("Port : " + PORT);
            System.out.println("Service : " + NOM_SERVICE);
            System.out.println("\nEn attente de connexions...\n");

            // Thread pour afficher les stats toutes les minutes
            Thread statsThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60000);
                        service.afficherStatistiques();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            statsThread.setDaemon(true);
            statsThread.start();

        } catch (Exception e) {
            System.err.println("Erreur de démarrage :");
            e.printStackTrace();
        }
    }
}
