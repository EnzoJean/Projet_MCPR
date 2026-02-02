package server.outils;

import common.IServiceOutils;
import common.IServiceAuthentification;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Implémentation du service de gestion des outils (squelette étape 2)
 */
public class ServiceOutilsImpl extends UnicastRemoteObject implements IServiceOutils {

    private IServiceAuthentification serviceAuth;

    public ServiceOutilsImpl() throws RemoteException {
        super();
        System.out.println("[Outils] Service démarré");
        connecterAuServeurAuth();
    }

    /**
     * Établit la connexion au serveur d'authentification
     */
    private void connecterAuServeurAuth() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            serviceAuth = (IServiceAuthentification) registry.lookup("ServiceAuthentification");
            System.out.println("[Outils] Connecté au serveur d'authentification");
        } catch (Exception e) {
            System.err.println("[Outils] Serveur d'auth non disponible");
        }
    }

    @Override
    public String testerConnexion() throws RemoteException {
        return "Serveur d'outils opérationnel";
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
