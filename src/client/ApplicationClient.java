package client;

import common.IServiceAuthentification;
import common.IServiceOutils;
import common.IServiceStockage;
import common.modeles.Utilisateur;
import common.modeles.Jeton;
import common.modeles.Outil;
import common.modeles.CategorieOutil;
import common.modeles.LocalStockage;

import java.util.List;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/* Application cliente pour PART'TOOL
   Permet inscription, connexion et tests des serveurs */
public class ApplicationClient {

    private IServiceAuthentification serviceAuth;
    private IServiceOutils serviceOutils;
    private IServiceStockage serviceStockage;
    private Scanner scanner;

    private Jeton jetonCourant;
    private Utilisateur utilisateurCourant;

    public ApplicationClient() {
        this.scanner = new Scanner(System.in);
        connecterAuxServeurs();
    }

    /* Connexion RMI aux trois serveurs */
    private void connecterAuxServeurs() {
        try {
            System.out.println("Connexion aux serveurs...");

            Registry registreAuth = LocateRegistry.getRegistry("localhost", 1099);
            serviceAuth = (IServiceAuthentification) registreAuth.lookup("ServiceAuthentification");
            System.out.println("Serveur d'authentification");

            try {
                Registry registreOutils = LocateRegistry.getRegistry("localhost", 1100);
                serviceOutils = (IServiceOutils) registreOutils.lookup("ServiceOutils");
                System.out.println("Serveur d'outils");
            } catch (Exception e) {
                System.out.println("Serveur d'outils non disponible");
            }

            try {
                Registry registreStockage = LocateRegistry.getRegistry("localhost", 1101);
                serviceStockage = (IServiceStockage) registreStockage.lookup("ServiceStockage");
                System.out.println("Serveur de stockage");
            } catch (Exception e) {
                System.out.println("Serveur de stockage non disponible");
            }

            System.out.println();

        } catch (Exception e) {
            System.err.println("Impossible de se connecter au serveur d'authentification !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* Affiche le menu principal */
    private void afficherMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       PART'TOOL - Menu Principal       ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (utilisateurCourant != null) {
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ Connecté : " + utilisateurCourant.getPrenom() + " " + utilisateurCourant.getNom());
            System.out.println("│ Carte : " + utilisateurCourant.getCarteAcces());
            System.out.println("└────────────────────────────────────────┘");
        }

        System.out.println("\n  1. S'inscrire");
        System.out.println("  2. Se connecter");

        if (utilisateurCourant != null) {
            System.out.println("\n  ═══ Gestion des Outils ═══");
            System.out.println("  3. Déclarer un outil");
            System.out.println("  4. Consulter tous les outils");
            System.out.println("  5. Consulter par catégorie");
            System.out.println("  6. Rechercher un outil");
            System.out.println("  7. Mes outils");
            System.out.println("\n  ═══ Gestion des Locaux ═══");
            System.out.println("  10. Consulter les locaux");
            System.out.println("  11. Détails d'un local");
            System.out.println("\n  ═══ Compte ═══");
            System.out.println("  8. Mes informations");
            System.out.println("  9. Tester les serveurs");
            System.out.println("  0. Se déconnecter");
        } else {
            System.out.println("  0. Quitter");
        }

        System.out.print("\nChoix : ");
    }

    /* Inscription d'un nouvel utilisateur */
    private void inscrire() {
        System.out.println("\n═══ Inscription ═══");

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();

        System.out.print("Email : ");
        String email = scanner.nextLine();

        try {
            Utilisateur user = serviceAuth.inscrire(nom, prenom, email);

            System.out.println("\nInscription réussie !");
            System.out.println("┌─────────────────────────────────────────┐");
            System.out.println("│ CONSERVEZ CES INFORMATIONS :           │");
            System.out.println("│                                         │");
            System.out.println("│ Carte d'accès : " + user.getCarteAcces());
            System.out.println("│ Code secret   : " + user.getCode());
            System.out.println("└─────────────────────────────────────────┘");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Connexion d'un utilisateur existant */
    private void seConnecter() {
        System.out.println("\n═══ Connexion ═══");

        System.out.print("Carte d'accès : ");
        long carte;
        try {
            carte = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Numéro invalide");
            return;
        }

        System.out.print("Code secret : ");
        String code = scanner.nextLine();

        try {
            Jeton jeton = serviceAuth.seConnecter(carte, code);

            if (jeton == null) {
                System.err.println("Identifiants incorrects");
                return;
            }

            jetonCourant = jeton;
            utilisateurCourant = serviceAuth.obtenirUtilisateurParJeton(jeton.getValeur());

            System.out.println("\nConnexion réussie !");
            System.out.println("Bienvenue " + utilisateurCourant.getPrenom() + " !");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Teste la connexion aux serveurs */
    private void testerServeurs() {
        System.out.println("\n═══ Test des Serveurs ═══");

        if (serviceOutils != null) {
            try {
                System.out.println("Outils : " + serviceOutils.testerConnexion());
            } catch (Exception e) {
                System.err.println("Outils : Erreur");
            }
        } else {
            System.out.println("Outils : Non connecté");
        }

        if (serviceStockage != null) {
            try {
                System.out.println("Stockage : " + serviceStockage.testerConnexion());
            } catch (Exception e) {
                System.err.println("Stockage : Erreur");
            }
        } else {
            System.out.println("Stockage : Non connecté");
        }

        if (jetonCourant != null) {
            try {
                boolean valide = serviceAuth.verifierJeton(jetonCourant.getValeur());
                System.out.println("Jeton : " + (valide ? "Valide" : "Invalide"));
            } catch (Exception e) {
                System.err.println("Jeton : Erreur");
            }
        }
    }

    /* Affiche les informations de l'utilisateur connecté */
    private void afficherInfos() {
        if (utilisateurCourant == null) {
            System.out.println("Non connecté");
            return;
        }

        System.out.println("\n═══ Mes Informations ═══");
        System.out.println("Nom : " + utilisateurCourant.getNom());
        System.out.println("Prénom : " + utilisateurCourant.getPrenom());
        System.out.println("Email : " + utilisateurCourant.getEmail());
        System.out.println("Carte : " + utilisateurCourant.getCarteAcces());
        System.out.println("Inscrit le : " + utilisateurCourant.getDateInscription());

        if (jetonCourant != null) {
            System.out.println("\nSession :");
            System.out.println("  Jeton : " + jetonCourant.getValeur());
            System.out.println("  Expire : " + jetonCourant.getDateExpiration());
            System.out.println("  Statut : " + (jetonCourant.estValide() ? "Active" : "Expirée"));
        }
    }

    /* Déconnexion de l'utilisateur */
    private void seDeconnecter() {
        if (jetonCourant != null) {
            try {
                serviceAuth.seDeconnecter(jetonCourant.getValeur());
                System.out.println("Déconnexion réussie");
            } catch (Exception e) {
                System.err.println("Erreur : " + e.getMessage());
            }
        }

        jetonCourant = null;
        utilisateurCourant = null;
    }

    /* Déclare un nouvel outil */
    private void declarerOutil() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        System.out.println("\n═══ Déclaration d'Outil ═══");

        System.out.print("Nom de l'outil : ");
        String nom = scanner.nextLine();

        System.out.print("Usage : ");
        String usage = scanner.nextLine();

        System.out.print("Description : ");
        String description = scanner.nextLine();

        System.out.print("Poids (kg) : ");
        double poids;
        try {
            poids = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Poids invalide");
            return;
        }

        System.out.print("Dimensions (ex: 30x20x10 cm) : ");
        String dimensions = scanner.nextLine();

        System.out.println("\nCatégories disponibles :");
        CategorieOutil[] categories = CategorieOutil.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println("  " + (i + 1) + ". " + categories[i].getLibelle());
        }
        System.out.print("Choix : ");
        int choixCat;
        try {
            choixCat = Integer.parseInt(scanner.nextLine());
            if (choixCat < 1 || choixCat > categories.length) {
                System.err.println("Catégorie invalide");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Numéro invalide");
            return;
        }

        try {
            Outil outil = serviceOutils.declarerOutil(jetonCourant.getValeur(), nom, usage,
                    description, poids, dimensions, categories[choixCat - 1]);

            System.out.println("\n[OK] Outil declare avec succes !");
            System.out.println("┌─────────────────────────────────────────┐");
            System.out.println("│ QR Code : " + outil.getQrCode());
            System.out.println("│ Nom     : " + outil.getNom());
            System.out.println("│ État    : " + outil.getEtat().getLibelle());
            System.out.println("└─────────────────────────────────────────┘");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Consulte tous les locaux de stockage */
    private void consulterLocaux() {
        System.out.println("\n═══ Tous les Locaux de Stockage ═══");

        try {
            List<LocalStockage> locaux = serviceStockage.consulterTousLocaux(jetonCourant.getValeur());

            if (locaux.isEmpty()) {
                System.out.println("Aucun local enregistré");
            } else {
                System.out.println("\n" + locaux.size() + " local/locaux disponible(s) :\n");
                for (LocalStockage local : locaux) {
                    System.out.println(local);
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Affiche les détails d'un local spécifique */
    private void afficherDetailsLocal() {
        System.out.println("\n═══ Détails d'un Local ═══");

        System.out.print("ID du local : ");
        long idLocal;
        try {
            idLocal = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("ID invalide");
            return;
        }

        try {
            LocalStockage local = serviceStockage.obtenirLocal(jetonCourant.getValeur(), idLocal);

            if (local == null) {
                System.err.println("Local introuvable");
                return;
            }

            System.out.println();
            System.out.println(local);

            List<Long> outils = serviceStockage.consulterOutilsLocal(jetonCourant.getValeur(), idLocal);
            System.out.println("\nOutils stockés : " + outils.size());
            if (!outils.isEmpty()) {
                for (Long qrCode : outils) {
                    System.out.println("  - Outil QR#" + qrCode);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Consulte tous les outils disponibles */
    private void consulterTousOutils() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        try {
            List<Outil> outils = serviceOutils.consulterTousOutils(jetonCourant.getValeur());

            System.out.println("\n═══ Tous les Outils (" + outils.size() + ") ═══\n");

            if (outils.isEmpty()) {
                System.out.println("Aucun outil déclaré");
            } else {
                for (Outil outil : outils) {
                    System.out.println(outil);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    private void consulterOutilsParCategorie() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        System.out.println("\n═══ Consultation par Catégorie ═══");
        System.out.println("\nCatégories disponibles :");
        CategorieOutil[] categories = CategorieOutil.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println("  " + (i + 1) + ". " + categories[i].getLibelle());
        }
        System.out.print("Choix : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine());
            if (choix < 1 || choix > categories.length) {
                System.err.println("Catégorie invalide");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Numéro invalide");
            return;
        }

        try {
            CategorieOutil categorie = categories[choix - 1];
            List<Outil> outils = serviceOutils.consulterOutilsParCategorie(
                    jetonCourant.getValeur(), categorie);

            System.out.println("\n═══ " + categorie.getLibelle() + " (" + outils.size() + ") ═══\n");

            if (outils.isEmpty()) {
                System.out.println("Aucun outil dans cette catégorie");
            } else {
                for (Outil outil : outils) {
                    System.out.println(outil);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Recherche des outils par mot-clé */
    private void rechercherOutils() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        System.out.println("\n═══ Recherche d'Outils ═══");
        System.out.print("Mot-clé : ");
        String motCle = scanner.nextLine();

        if (motCle.trim().isEmpty()) {
            System.err.println("Mot-clé vide");
            return;
        }

        try {
            List<Outil> outils = serviceOutils.rechercherOutils(jetonCourant.getValeur(), motCle);

            System.out.println("\n═══ Résultats (" + outils.size() + ") ═══\n");

            if (outils.isEmpty()) {
                System.out.println("Aucun outil trouvé");
            } else {
                for (Outil outil : outils) {
                    System.out.println(outil);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Consulte les outils de l'utilisateur */
    private void consulterMesOutils() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        try {
            List<Outil> outils = serviceOutils.consulterMesOutils(jetonCourant.getValeur());

            System.out.println("\n═══ Mes Outils (" + outils.size() + ") ═══\n");

            if (outils.isEmpty()) {
                System.out.println("Vous n'avez déclaré aucun outil");
            } else {
                for (Outil outil : outils) {
                    System.out.println(outil);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Boucle principale de l'application */
    public void demarrer() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   Bienvenue sur PART'TOOL");
        System.out.println("═══════════════════════════════════════════");

        boolean continuer = true;

        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    inscrire();
                    break;
                case "2":
                    seConnecter();
                    break;
                case "3":
                    if (utilisateurCourant != null)
                        declarerOutil();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "4":
                    if (utilisateurCourant != null)
                        consulterTousOutils();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "5":
                    if (utilisateurCourant != null)
                        consulterOutilsParCategorie();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "6":
                    if (utilisateurCourant != null)
                        rechercherOutils();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "7":
                    if (utilisateurCourant != null)
                        consulterMesOutils();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "8":
                    if (utilisateurCourant != null)
                        afficherInfos();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "9":
                    if (utilisateurCourant != null)
                        testerServeurs();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "10":
                    if (utilisateurCourant != null)
                        consulterLocaux();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "11":
                    if (utilisateurCourant != null)
                        afficherDetailsLocal();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "0":
                    if (utilisateurCourant != null) {
                        seDeconnecter();
                        System.out.println("\nAu revoir !");
                        continuer = false;
                    } else {
                        System.out.println("\nAu revoir !");
                        continuer = false;
                    }
                    break;
                default:
                    System.out.println("Choix invalide");
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        ApplicationClient client = new ApplicationClient();
        client.demarrer();
    }
}
