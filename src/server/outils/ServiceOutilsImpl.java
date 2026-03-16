package server.outils;

import common.IServiceOutils;
import common.IServiceAuthentification;
import common.IServiceStockage;
import common.modeles.Outil;
import common.modeles.Emprunt;
import common.modeles.Utilisateur;
import common.modeles.CategorieOutil;
import common.modeles.EtatOutil;
import common.modeles.StatistiquesOutils;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

/* Implémentation du service de gestion des outils */
public class ServiceOutilsImpl extends UnicastRemoteObject implements IServiceOutils {

    private IServiceAuthentification serviceAuth;
    private IServiceStockage serviceStockage;
    private HashMap<Long, Outil> outils;
    private List<Emprunt> emprunts;
    private Random random;

    public ServiceOutilsImpl() throws RemoteException {
        super();
        this.outils = GestionOutils.charger();
        this.emprunts = GestionOutils.chargerEmprunts();
        this.random = new Random();
        System.out.println("[Outils] Service démarré avec " + outils.size() + " outil(s)");
        connecterAuServeurAuth();
        connecterAuServeurStockage();
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

    /* Établit la connexion au serveur de stockage */
    private void connecterAuServeurStockage() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1101);
            serviceStockage = (IServiceStockage) registry.lookup("ServiceStockage");
            System.out.println("[Outils] Connecté au serveur de stockage");
        } catch (Exception e) {
            System.err.println("[Outils] Serveur de stockage non disponible (emprunt/restitution limités)");
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

    @Override
    public List<Outil> consulterOutilsDisponibles(String jeton) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);
        return outils.values().stream()
                .filter(o -> o.getEtat() == EtatOutil.DISPONIBLE)
                .collect(Collectors.toList());
    }

    @Override
    public boolean emprunterOutil(String jeton, long qrCode, long idLocal) throws RemoteException {
        Utilisateur utilisateur = verifierEtObtenirUtilisateur(jeton);

        Outil outil = outils.get(qrCode);
        if (outil == null) {
            throw new RemoteException("Outil QR#" + qrCode + " introuvable");
        }

        if (outil.getEtat() != EtatOutil.DISPONIBLE) {
            throw new RemoteException("L'outil '" + outil.getNom() + "' n'est pas disponible (état : " + outil.getEtat().getLibelle() + ")");
        }

        /* Notifie le serveur de stockage pour retirer l'outil du local */
        if (serviceStockage != null) {
            boolean retire = serviceStockage.enregistrerEmprunt(jeton, qrCode, idLocal);
            if (!retire) {
                throw new RemoteException("Impossible de retirer l'outil du local #" + idLocal + ". Vérifiez que l'outil y est bien stocké.");
            }
        }

        /* Met à jour l'état de l'outil */
        outil.setEtat(EtatOutil.EMPRUNTE);
        outil.setEmpruntePar(utilisateur.getCarteAcces());
        outil.setLocalStockageId(null);
        GestionOutils.sauvegarder(outils);

        /* Enregistre l'emprunt dans l'historique */
        Emprunt emprunt = new Emprunt(qrCode, utilisateur.getCarteAcces(), idLocal);
        emprunts.add(emprunt);
        GestionOutils.sauvegarderEmprunts(emprunts);

        System.out.println("[Outils] Emprunt : QR#" + qrCode + " (" + outil.getNom() + ") par " +
                utilisateur.getPrenom() + " " + utilisateur.getNom() + " depuis local #" + idLocal);
        return true;
    }

    @Override
    public boolean restituerOutil(String jeton, long qrCode, long idLocal) throws RemoteException {
        Utilisateur utilisateur = verifierEtObtenirUtilisateur(jeton);

        Outil outil = outils.get(qrCode);
        if (outil == null) {
            throw new RemoteException("Outil QR#" + qrCode + " introuvable");
        }

        if (outil.getEtat() != EtatOutil.EMPRUNTE) {
            throw new RemoteException("L'outil '" + outil.getNom() + "' n'est pas en cours d'emprunt");
        }

        if (outil.getEmpruntePar() != utilisateur.getCarteAcces()) {
            throw new RemoteException("Vous n'avez pas emprunté cet outil");
        }

        /* Notifie le serveur de stockage pour réintégrer l'outil dans le local */
        if (serviceStockage != null) {
            serviceStockage.enregistrerRestitution(jeton, qrCode, idLocal);
        }

        /* Met à jour l'état de l'outil */
        outil.setEtat(EtatOutil.DISPONIBLE);
        outil.setEmpruntePar(null);
        outil.setLocalStockageId(idLocal);
        GestionOutils.sauvegarder(outils);

        /* Marque l'emprunt comme restitué dans l'historique */
        for (Emprunt emprunt : emprunts) {
            if (emprunt.getQrCode() == qrCode &&
                    emprunt.getCarteEmprunteur() == utilisateur.getCarteAcces() &&
                    emprunt.estEnCours()) {
                emprunt.restituer();
                break;
            }
        }
        GestionOutils.sauvegarderEmprunts(emprunts);

        System.out.println("[Outils] Restitution : QR#" + qrCode + " (" + outil.getNom() + ") par " +
                utilisateur.getPrenom() + " " + utilisateur.getNom() + " dans local #" + idLocal);
        return true;
    }

    @Override
    public List<Emprunt> consulterMesEmprunts(String jeton) throws RemoteException {
        Utilisateur utilisateur = verifierEtObtenirUtilisateur(jeton);
        return emprunts.stream()
                .filter(e -> e.getCarteEmprunteur() == utilisateur.getCarteAcces())
                .collect(Collectors.toList());
    }

    @Override
    public StatistiquesOutils obtenirStatistiques(String jeton) throws RemoteException {
        verifierEtObtenirUtilisateur(jeton);

        int nbTotal = outils.size();
        int nbDisponibles = (int) outils.values().stream()
                .filter(o -> o.getEtat() == EtatOutil.DISPONIBLE).count();
        int nbEmpruntes = (int) outils.values().stream()
                .filter(o -> o.getEtat() == EtatOutil.EMPRUNTE).count();
        int nbEmpruntsTotal = emprunts.size();
        int nbEmpruntsEnCours = (int) emprunts.stream().filter(Emprunt::estEnCours).count();

        /* Répartition par catégorie */
        Map<String, Integer> parCategorie = new LinkedHashMap<>();
        for (CategorieOutil cat : CategorieOutil.values()) {
            int nb = (int) outils.values().stream()
                    .filter(o -> o.getCategorie() == cat).count();
            if (nb > 0) parCategorie.put(cat.getLibelle(), nb);
        }

        /* Nombre d'emprunts par utilisateur (carte) */
        Map<Long, Integer> parUtilisateur = new HashMap<>();
        for (Emprunt e : emprunts) {
            parUtilisateur.merge(e.getCarteEmprunteur(), 1, Integer::sum);
        }

        System.out.println("[Outils] Statistiques consultées");
        return new StatistiquesOutils(nbTotal, nbDisponibles, nbEmpruntes,
                nbEmpruntsTotal, nbEmpruntsEnCours, parCategorie, parUtilisateur);
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
