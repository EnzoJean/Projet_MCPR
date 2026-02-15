package server.outils;

import common.IServiceOutils;
import common.IServiceAuthentification;
import common.modeles.Outil;
import common.modeles.Utilisateur;
import common.modeles.CategorieOutil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

/* Implémentation du service de gestion des outils */
public class ServiceOutilsImpl extends UnicastRemoteObject implements IServiceOutils {

    private IServiceAuthentification serviceAuth;
    private HashMap<Long, Outil> outils;
    private Random random;

    public ServiceOutilsImpl() throws RemoteException {
        super();
        this.outils = GestionOutils.charger();
        this.random = new Random();
        System.out.println("[Outils] Service démarré avec " + outils.size() + " outil(s)");
        connecterAuServeurAuth();
    }

    /* Établit la connexion au serveur d'authentification */
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

    @Override
    public Outil declarerOutil(String jeton, String nom, String usage, String description,
            double poids, String dimensions, CategorieOutil categorie) throws RemoteException {
        Utilisateur utilisateur = verifierEtObtenirUtilisateur(jeton);

        long qrCode = genererQRCode();
        Outil outil = new Outil(qrCode, nom, usage, description, poids, dimensions,
                utilisateur.getCarteAcces(), categorie);

        outils.put(qrCode, outil);
        GestionOutils.sauvegarder(outils);

        System.out.println("[Outils] Déclaration : " + nom + " (QR#" + qrCode + ") par " +
                utilisateur.getPrenom() + " " + utilisateur.getNom());
        return outil;
    }

    @Override
    public List<Outil> consulterTousOutils(String jeton) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return new ArrayList<>(outils.values());
    }

    @Override
    public List<Outil> consulterOutilsParCategorie(String jeton, CategorieOutil categorie) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return outils.values().stream()
                .filter(o -> o.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    @Override
    public List<Outil> rechercherOutils(String jeton, String motCle) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        String motCleLower = motCle.toLowerCase();
        return outils.values().stream()
                .filter(o -> o.getNom().toLowerCase().contains(motCleLower) ||
                        o.getUsage().toLowerCase().contains(motCleLower) ||
                        o.getDescription().toLowerCase().contains(motCleLower))
                .collect(Collectors.toList());
    }

    @Override
    public Outil obtenirOutil(String jeton, long qrCode) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return outils.get(qrCode);
    }

    @Override
    public List<Outil> consulterMesOutils(String jeton) throws RemoteException {
        Utilisateur utilisateur = verifierEtObtenirUtilisateur(jeton);
        return outils.values().stream()
                .filter(o -> o.getProprietaire() == utilisateur.getCarteAcces())
                .collect(Collectors.toList());
    }

    /* Vérifie un jeton et retourne l'utilisateur associé */
    private Utilisateur verifierEtObtenirUtilisateur(String jeton) throws RemoteException {
        if (serviceAuth == null) {
            throw new RemoteException("Service d'authentification non disponible");
        }

        if (!serviceAuth.verifierJeton(jeton)) {
            throw new RemoteException("Jeton invalide ou expiré");
        }

        Utilisateur utilisateur = serviceAuth.obtenirUtilisateurParJeton(jeton);
        if (utilisateur == null) {
            throw new RemoteException("Utilisateur non trouvé");
        }

        return utilisateur;
    }

    /* Génère un QR code unique basé sur le timestamp */
    private long genererQRCode() {
        long qrCode = System.currentTimeMillis() + random.nextInt(100000);
        while (outils.containsKey(qrCode)) {
            qrCode = System.currentTimeMillis() + random.nextInt(100000);
        }
        return qrCode;
    }
}
