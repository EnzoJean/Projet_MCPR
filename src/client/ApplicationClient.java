package client;

import common.IServiceAuthentification;
import common.IServiceOutils;
import common.IServiceStockage;
import common.modeles.Utilisateur;
import common.modeles.Jeton;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Application cliente pour PART'TOOL
 * Permet inscription, connexion et tests des serveurs
 */
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

    /**
     * Connexion RMI aux trois serveurs
     */
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

    /**
     * Affiche le menu principal
     */
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
            System.out.println("  3. Tester les serveurs");
            System.out.println("  4. Mes informations");
            System.out.println("  5. Se déconnecter");
        }

        System.out.println("  0. Quitter");
        System.out.print("\nChoix : ");
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
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

    /**
     * Connexion d'un utilisateur existant
     */
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

    /**
     * Teste la connexion aux serveurs
     */
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

    /**
     * Affiche les informations de l'utilisateur connecté
     */
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

    /**
     * Déconnexion de l'utilisateur
     */
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

    /**
     * Boucle principale de l'application
     */
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
                        testerServeurs();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "4":
                    if (utilisateurCourant != null)
                        afficherInfos();
                    else
                        System.out.println("Connectez-vous d'abord");
                    break;
                case "5":
                    if (utilisateurCourant != null)
                        seDeconnecter();
                    else
                        System.out.println("Non connecté");
                    break;
                case "0":
                    if (utilisateurCourant != null)
                        seDeconnecter();
                    System.out.println("\nAu revoir !");
                    continuer = false;
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
