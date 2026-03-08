package server.stockage;

import common.IServiceStockage;
import common.IServiceAuthentification;
import common.modeles.LocalStockage;
import common.modeles.Utilisateur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/* Implémentation du service de gestion des locaux de stockage */
public class ServiceStockageImpl extends UnicastRemoteObject implements IServiceStockage {

    private IServiceAuthentification serviceAuth;
    private Map<Long, LocalStockage> locaux;

    public ServiceStockageImpl() throws RemoteException {
        super();
        this.locaux = GestionLocaux.charger();
        System.out.println("[Stockage] Service démarré avec " + locaux.size() + " local/locaux");
        connecterAuServeurAuth();
    }

    /* Établit la connexion au serveur d'authentification */
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
        return "Serveur de stockage opérationnel";
    }

    @Override
    public boolean deverrouillerLocal(long carteAcces, String code, long idLocal) throws RemoteException {
        if (serviceAuth == null) {
            throw new RemoteException("Service d'authentification non disponible");
        }

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            System.out.println("[Stockage] Local #" + idLocal + " introuvable");
            return false;
        }

        Utilisateur utilisateur = serviceAuth.obtenirUtilisateurParJeton(
                serviceAuth.seConnecter(carteAcces, code).getValeur());

        if (utilisateur == null) {
            System.out.println("[Stockage] Accès refusé : identifiants incorrects");
            return false;
        }

        if (!local.estAutorise(carteAcces)) {
            local.ajouterUtilisateurAutorise(carteAcces);
            GestionLocaux.sauvegarder(locaux);
        }

        System.out.println("[Stockage] Déverrouillage local #" + idLocal + " par " +
                utilisateur.getPrenom() + " " + utilisateur.getNom());
        return true;
    }

    @Override
    public List<LocalStockage> consulterTousLocaux(String jeton) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return new ArrayList<>(locaux.values());
    }

    @Override
    public LocalStockage obtenirLocal(String jeton, long idLocal) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return locaux.get(idLocal);
    }

    @Override
    public boolean ajouterOutilLocal(String jeton, long idLocal, long qrCodeOutil) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            return false;
        }

        if (local.getNbOutilsStockes() >= local.getCapaciteMax()) {
            System.out.println("[Stockage] Local #" + idLocal + " plein");
            return false;
        }

        local.ajouterOutil(qrCodeOutil);
        GestionLocaux.sauvegarder(locaux);
        System.out.println("[Stockage] Outil QR#" + qrCodeOutil + " ajouté au local #" + idLocal);
        return true;
    }

    @Override
    public boolean retirerOutilLocal(String jeton, long idLocal, long qrCodeOutil) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            return false;
        }

        local.retirerOutil(qrCodeOutil);
        GestionLocaux.sauvegarder(locaux);
        System.out.println("[Stockage] Outil QR#" + qrCodeOutil + " retiré du local #" + idLocal);
        return true;
    }

    @Override
    public List<Long> consulterOutilsLocal(String jeton, long idLocal) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(local.getOutilsStockes());
    }

    @Override
    public boolean enregistrerEmprunt(String jeton, long qrCode, long idLocal) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            System.err.println("[Stockage] Local #" + idLocal + " introuvable pour emprunt");
            return false;
        }

        if (!local.getOutilsStockes().contains(qrCode)) {
            System.err.println("[Stockage] Outil QR#" + qrCode + " non présent dans local #" + idLocal);
            return false;
        }

        local.retirerOutil(qrCode);
        GestionLocaux.sauvegarder(locaux);
        System.out.println("[Stockage] Emprunt enregistré : QR#" + qrCode + " retiré du local #" + idLocal);
        return true;
    }

    @Override
    public boolean enregistrerRestitution(String jeton, long qrCode, long idLocal) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        LocalStockage local = locaux.get(idLocal);
        if (local == null) {
            System.err.println("[Stockage] Local #" + idLocal + " introuvable pour restitution");
            return false;
        }

        if (local.getNbOutilsStockes() >= local.getCapaciteMax()) {
            System.err.println("[Stockage] Local #" + idLocal + " plein, restitution impossible");
            return false;
        }

        local.ajouterOutil(qrCode);
        GestionLocaux.sauvegarder(locaux);
        System.out.println("[Stockage] Restitution enregistrée : QR#" + qrCode + " ajouté au local #" + idLocal);
        return true;
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
}
