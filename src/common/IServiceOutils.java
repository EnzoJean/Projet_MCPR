package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI du serveur de gestion des outils (squelette pour l'étape 2)
 */
public interface IServiceOutils extends Remote {

    /**
     * Teste la connexion au serveur
     */
    String testerConnexion() throws RemoteException;
}
