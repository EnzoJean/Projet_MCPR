package server.stockage;

import common.IServiceStockage;
import common.IServiceAuthentification;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Implémentation du service de gestion d'un local (squelette étape 3)
 */
public class ServiceStockageImpl extends UnicastRemoteObject implements IServiceStockage {

    private IServiceAuthentification serviceAuth;
    private int idLocal;

    public ServiceStockageImpl(int idLocal) throws RemoteException {
        super();
        this.idLocal = idLocal;
        System.out.println("[Stockage] Service démarré pour local " + idLocal);
        connecterAuServeurAuth();
    }

    /**
     * Établit la connexion au serveur d'authentification
     */
    private void connecterAuServeurAuth() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            serviceAuth = (IServiceAuthentification) registry.lookup("ServiceAuthentification");
            System.out.println("[Stockage] Connecté au serveur d'authentification");
        } catch (Exception e) {
            System.err.println("[Stockage] Serveur d'auth non disponible");
        }
    }

    @Override
    public String testerConnexion() throws RemoteException {
        return "Serveur local " + idLocal + " opérationnel";
    }

    /**
     * Vérifie un jeton via le serveur d'authentification
     */
    protected boolean verifierJeton(String jeton) throws RemoteException {
        if (serviceAuth == null) {
            throw new RemoteException("Service d'authentification non disponible");
        }
        return serviceAuth.verifierJeton(jeton);
    }
}
