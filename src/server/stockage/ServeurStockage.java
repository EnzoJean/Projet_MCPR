package server.stockage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Lance le serveur de gestion d'un local RMI sur le port 1101
 */
public class ServeurStockage {

    public static final int PORT = 1101;
    public static final String NOM_SERVICE = "ServiceStockage";

    public static void main(String[] args) {
        try {
            int idLocal = (args.length > 0) ? Integer.parseInt(args[0]) : 1;

            System.out.println("==============================================");
            System.out.println("  PART'TOOL - Serveur Local #" + idLocal);
            System.out.println("==============================================\n");

            ServiceStockageImpl service = new ServiceStockageImpl(idLocal);
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind(NOM_SERVICE, service);

            System.out.println("Serveur démarré sur port " + PORT);

        } catch (Exception e) {
            System.err.println("Erreur de démarrage :");
            e.printStackTrace();
        }
    }
}
