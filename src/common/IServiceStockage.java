package common;

import common.modeles.LocalStockage;
import common.modeles.StatistiquesStockage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/* Interface RMI du serveur de gestion des locaux de stockage */
public interface IServiceStockage extends Remote {

    /* Teste la connexion au serveur */
    String testerConnexion() throws RemoteException;

    /* Déverrouille un local avec carte d'accès et code */
    boolean deverrouillerLocal(long carteAcces, String code, long idLocal) throws RemoteException;

    /* Consulte tous les locaux de stockage */
    List<LocalStockage> consulterTousLocaux(String jeton) throws RemoteException;

    /* Obtient un local spécifique par son ID */
    LocalStockage obtenirLocal(String jeton, long idLocal) throws RemoteException;

    /* Ajoute un outil à un local */
    boolean ajouterOutilLocal(String jeton, long idLocal, long qrCodeOutil) throws RemoteException;

    /* Retire un outil d'un local */
    boolean retirerOutilLocal(String jeton, long idLocal, long qrCodeOutil) throws RemoteException;

    /* Consulte les outils stockés dans un local */
    List<Long> consulterOutilsLocal(String jeton, long idLocal) throws RemoteException;

    /* Enregistre l'emprunt d'un outil (retire l'outil du local) */
    boolean enregistrerEmprunt(String jeton, long qrCode, long idLocal) throws RemoteException;

    /* Enregistre la restitution d'un outil (réintègre l'outil dans le local) */
    boolean enregistrerRestitution(String jeton, long qrCode, long idLocal) throws RemoteException;

    /* Retourne les statistiques du serveur de stockage */
    StatistiquesStockage obtenirStatistiques(String jeton) throws RemoteException;
}
