package server.outils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/* Lance le serveur de gestion des outils RMI sur le port 1100 */
public class ServeurOutils {

    public static final int PORT = 1100;
    public static final String NOM_SERVICE = "ServiceOutils";

    public static void main(String[] args) {
        try {
            System.out.println("==============================================");
            System.out.println("  PART'TOOL - Serveur Outils");
            System.out.println("==============================================\n");

            ServiceOutilsImpl service = new ServiceOutilsImpl();
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind(NOM_SERVICE, service);

            System.out.println("Serveur démarré sur port " + PORT);

        } catch (Exception e) {
            System.err.println("Erreur de démarrage :");
            e.printStackTrace();
        }
    }
}
