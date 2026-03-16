package common;

import common.modeles.Outil;
import common.modeles.Emprunt;
import common.modeles.CategorieOutil;
import common.modeles.StatistiquesOutils;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/* Interface RMI du serveur de gestion des outils */
public interface IServiceOutils extends Remote {

    /* Teste la connexion au serveur */
    String testerConnexion() throws RemoteException;

    /* Déclare un nouvel outil */
    Outil declarerOutil(String jeton, String nom, String usage, String description,
            double poids, String dimensions, CategorieOutil categorie) throws RemoteException;

    /* Consulte tous les outils */
    List<Outil> consulterTousOutils(String jeton) throws RemoteException;

    /* Consulte les outils d'une catégorie spécifique */
    List<Outil> consulterOutilsParCategorie(String jeton, CategorieOutil categorie) throws RemoteException;

    /* Recherche des outils par mot-clé */
    List<Outil> rechercherOutils(String jeton, String motCle) throws RemoteException;

    /* Obtient un outil spécifique par son QR code */
    Outil obtenirOutil(String jeton, long qrCode) throws RemoteException;

    /* Consulte les outils déclarés par l'utilisateur connecté */
    List<Outil> consulterMesOutils(String jeton) throws RemoteException;

    /* Consulte les outils disponibles à l'emprunt (état DISPONIBLE) */
    List<Outil> consulterOutilsDisponibles(String jeton) throws RemoteException;

    /* Emprunte un outil depuis un local (scan QR code) */
    boolean emprunterOutil(String jeton, long qrCode, long idLocal) throws RemoteException;

    /* Restitue un outil dans un local (scan QR code) */
    boolean restituerOutil(String jeton, long qrCode, long idLocal) throws RemoteException;

    /* Consulte les emprunts en cours de l'utilisateur connecté */
    List<Emprunt> consulterMesEmprunts(String jeton) throws RemoteException;

    /* Retourne les statistiques du serveur d'outils */
    StatistiquesOutils obtenirStatistiques(String jeton) throws RemoteException;
}
