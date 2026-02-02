package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI du serveur de gestion des locaux (squelette pour l'étape 3)
 */
public interface IServiceStockage extends Remote {

    /**
     * Teste la connexion au serveur
     */
    String testerConnexion() throws RemoteException;
}
