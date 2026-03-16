package client;

import common.IServiceAuthentification;
import common.IServiceOutils;
import common.IServiceStockage;
import common.modeles.Jeton;
import common.modeles.StatistiquesOutils;
import common.modeles.StatistiquesStockage;
import common.modeles.Utilisateur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/* Client dédié à la consultation des statistiques du système PART'TOOL */
public class ApplicationClientStats {

    private IServiceAuthentification serviceAuth;
    private IServiceOutils serviceOutils;
    private IServiceStockage serviceStockage;
    private Scanner scanner;

    private Jeton jetonCourant;
    private Utilisateur utilisateurCourant;

    public ApplicationClientStats() {
        this.scanner = new Scanner(System.in);
        connecterAuxServeurs();
    }

    /* Connexion RMI aux trois serveurs */
    private void connecterAuxServeurs() {
        try {
            System.out.println("Connexion aux serveurs...");

            Registry registreAuth = LocateRegistry.getRegistry("localhost", 1099);
            serviceAuth = (IServiceAuthentification) registreAuth.lookup("ServiceAuthentification");
            System.out.println("Serveur d'authentification [OK]");

            try {
                Registry registreOutils = LocateRegistry.getRegistry("localhost", 1100);
                serviceOutils = (IServiceOutils) registreOutils.lookup("ServiceOutils");
                System.out.println("Serveur d'outils          [OK]");
            } catch (Exception e) {
                System.out.println("Serveur d'outils          [NON DISPONIBLE]");
            }

            try {
                Registry registreStockage = LocateRegistry.getRegistry("localhost", 1101);
                serviceStockage = (IServiceStockage) registreStockage.lookup("ServiceStockage");
                System.out.println("Serveur de stockage       [OK]");
            } catch (Exception e) {
                System.out.println("Serveur de stockage       [NON DISPONIBLE]");
            }

            System.out.println();

        } catch (Exception e) {
            System.err.println("Impossible de se connecter au serveur d'authentification !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* Connexion avec carte d'accès et code */
    private boolean seConnecter() {
        System.out.println("\n═══ Connexion requise ═══");

        System.out.print("Carte d'accès : ");
        long carte;
        try {
            carte = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Numéro invalide");
            return false;
        }

        System.out.print("Code secret : ");
        String code = scanner.nextLine();

        try {
            Jeton jeton = serviceAuth.seConnecter(carte, code);
            if (jeton == null) {
                System.err.println("Identifiants incorrects");
                return false;
            }
            jetonCourant = jeton;
            utilisateurCourant = serviceAuth.obtenirUtilisateurParJeton(jeton.getValeur());
            System.out.println("Connecté : " + utilisateurCourant.getPrenom() + " " + utilisateurCourant.getNom());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            return false;
        }
    }

    /* Affiche le menu de statistiques */
    private void afficherMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   PART'TOOL - Statistiques Système    ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (utilisateurCourant != null) {
            System.out.println("  Connecté : " + utilisateurCourant.getPrenom()
                    + " " + utilisateurCourant.getNom());
        }

        System.out.println();
        System.out.println("  1. Statistiques - Serveur d'outils");
        System.out.println("  2. Statistiques - Serveur de stockage");
        System.out.println("  3. Tableau de bord complet");
        System.out.println("  0. Quitter");
        System.out.print("\nChoix : ");
    }

    /* Affiche les statistiques du serveur d'outils */
    private void afficherStatsOutils() {
        if (serviceOutils == null) {
            System.err.println("Serveur d'outils non disponible");
            return;
        }

        try {
            StatistiquesOutils stats = serviceOutils.obtenirStatistiques(jetonCourant.getValeur());
            System.out.println("\n" + stats);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Affiche les statistiques du serveur de stockage */
    private void afficherStatsStockage() {
        if (serviceStockage == null) {
            System.err.println("Serveur de stockage non disponible");
            return;
        }

        try {
            StatistiquesStockage stats = serviceStockage.obtenirStatistiques(jetonCourant.getValeur());
            System.out.println("\n" + stats);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Affiche le tableau de bord combiné */
    private void afficherTableauDeBord() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Tableau de Bord Système          ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (serviceOutils != null) {
            try {
                StatistiquesOutils stats = serviceOutils.obtenirStatistiques(jetonCourant.getValeur());
                System.out.println("\n" + stats);
            } catch (Exception e) {
                System.err.println("[Outils] Erreur : " + e.getMessage());
            }
        } else {
            System.out.println("[Outils] Non disponible");
        }

        if (serviceStockage != null) {
            try {
                StatistiquesStockage stats = serviceStockage.obtenirStatistiques(jetonCourant.getValeur());
                System.out.println("\n" + stats);
            } catch (Exception e) {
                System.err.println("[Stockage] Erreur : " + e.getMessage());
            }
        } else {
            System.out.println("[Stockage] Non disponible");
        }
    }

    /* Boucle principale */
    public void demarrer() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   PART'TOOL - Client de Statistiques");
        System.out.println("═══════════════════════════════════════════");

        if (!seConnecter()) {
            System.out.println("Connexion échouée. Fermeture.");
            return;
        }

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    afficherStatsOutils();
                    break;
                case "2":
                    afficherStatsStockage();
                    break;
                case "3":
                    afficherTableauDeBord();
                    break;
                case "0":
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
        ApplicationClientStats client = new ApplicationClientStats();
        client.demarrer();
    }
}
