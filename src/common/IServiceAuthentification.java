package common;

import common.modeles.Utilisateur;
import common.modeles.Jeton;
import java.rmi.Remote;
import java.rmi.RemoteException;

/* Interface RMI du serveur d'authentification */
public interface IServiceAuthentification extends Remote {

    /* Inscrit un nouvel utilisateur et génère sa carte d'accès et son code */
    Utilisateur inscrire(String nom, String prenom, String email) throws RemoteException;

    /* Connecte un utilisateur et retourne un jeton de session valide 24h */
    Jeton seConnecter(long carteAcces, String code) throws RemoteException;

    /* Vérifie qu'un jeton est valide (utilisé par les autres serveurs) */
    boolean verifierJeton(String valeurJeton) throws RemoteException;

    /* Récupère l'utilisateur associé à un jeton valide */
    Utilisateur obtenirUtilisateurParJeton(String valeurJeton) throws RemoteException;

    /* Déconnecte un utilisateur en invalidant son jeton */
    void seDeconnecter(String valeurJeton) throws RemoteException;
}
