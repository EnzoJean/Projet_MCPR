package server.authentification;

import common.IServiceAuthentification;
import common.modeles.Utilisateur;
import common.modeles.Jeton;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/* Implémentation du serveur d'authentification
 * Gère les inscriptions, connexions et jetons de session */
public class ServiceAuthentificationImpl extends UnicastRemoteObject implements IServiceAuthentification {

    private HashMap<Long, Utilisateur> utilisateurs;
    private HashMap<String, Jeton> jetonsActifs;
    private Random random;
    private static final long DUREE_JETON_MS = 24 * 60 * 60 * 1000;

    public ServiceAuthentificationImpl() throws RemoteException {
        super();
        this.utilisateurs = GestionUtilisateurs.charger();
        this.jetonsActifs = new HashMap<>();
        this.random = new Random();
        System.out.println("[Auth] Serveur initialisé avec " + utilisateurs.size() + " compte(s)");
    }

    /* Inscrit un nouvel utilisateur avec génération automatique carte + code */
    @Override
    public Utilisateur inscrire(String nom, String prenom, String email) throws RemoteException {
        System.out.println("[Auth] Inscription : " + prenom + " " + nom);

        long carteAcces = genererNumeroCarte();
        String code = genererCode();
        Utilisateur utilisateur = new Utilisateur(carteAcces, code, nom, prenom, email);
        utilisateurs.put(carteAcces, utilisateur);

        GestionUtilisateurs.sauvegarder(utilisateurs);

        System.out.println("[Auth] Carte=" + carteAcces + ", Code=" + code);
        return utilisateur;
    }

    /* Connecte un utilisateur et génère un jeton de session */
    @Override
    public Jeton seConnecter(long carteAcces, String code) throws RemoteException {
        System.out.println("[Auth] Tentative connexion : Carte=" + carteAcces);

        Utilisateur utilisateur = utilisateurs.get(carteAcces);
        if (utilisateur == null) {
            System.out.println("[Auth] REFUSÉ : Carte inconnue");
            return null;
        }

        if (!utilisateur.getCode().equals(code)) {
            System.out.println("[Auth] REFUSÉ : Mauvais code");
            return null;
        }

        String valeurJeton = UUID.randomUUID().toString();
        Date expiration = new Date(System.currentTimeMillis() + DUREE_JETON_MS);
        Jeton jeton = new Jeton(valeurJeton, carteAcces, expiration);
        jetonsActifs.put(valeurJeton, jeton);

        System.out.println("[Auth] CONNEXION RÉUSSIE pour " + utilisateur.getPrenom() + " " + utilisateur.getNom());
        return jeton;
    }

    /* Vérifie qu'un jeton existe et n'est pas expiré */
    @Override
    public boolean verifierJeton(String valeurJeton) throws RemoteException {
        Jeton jeton = jetonsActifs.get(valeurJeton);

        if (jeton == null) {
            System.out.println("[Auth] Jeton inconnu");
            return false;
        }

        if (!jeton.estValide()) {
            System.out.println("[Auth] Jeton expiré");
            jetonsActifs.remove(valeurJeton);
            return false;
        }

        return true;
    }

    /* Retourne l'utilisateur associé à un jeton valide */
    @Override
    public Utilisateur obtenirUtilisateurParJeton(String valeurJeton) throws RemoteException {
        Jeton jeton = jetonsActifs.get(valeurJeton);
        if (jeton == null || !jeton.estValide()) {
            return null;
        }
        return utilisateurs.get(jeton.getCarteAcces());
    }

    /* Supprime un jeton pour déconnecter l'utilisateur */
    @Override
    public void seDeconnecter(String valeurJeton) throws RemoteException {
        jetonsActifs.remove(valeurJeton);
        System.out.println("[Auth] Déconnexion");
    }

    /* Génère un numéro de carte unique basé sur le timestamp */
    private long genererNumeroCarte() {
        long carte = System.currentTimeMillis() + random.nextInt(10000);
        while (utilisateurs.containsKey(carte)) {
            carte = System.currentTimeMillis() + random.nextInt(10000);
        }
        return carte;
    }

    /* Génère un code PIN à 6 chiffres */
    private String genererCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /* Affiche les statistiques du serveur */
    public void afficherStatistiques() {
        System.out.println("\n=== STATS ===");
        System.out.println("Utilisateurs : " + utilisateurs.size());
        System.out.println("Jetons actifs : " + jetonsActifs.size());
        System.out.println("=============\n");
    }
}
