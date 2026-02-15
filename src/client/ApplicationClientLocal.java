package client;

import common.IServiceStockage;
import common.IServiceAuthentification;
import common.modeles.LocalStockage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/* Client pour accès à un local de stockage spécifique */
public class ApplicationClientLocal {

    private IServiceStockage serviceStockage;
    private IServiceAuthentification serviceAuth;
    private Scanner scanner;
    private long idLocal;
    private boolean connecte;

    public ApplicationClientLocal(long idLocal) {
        this.idLocal = idLocal;
        this.scanner = new Scanner(System.in);
        this.connecte = false;
        connecterAuxServeurs();
    }

    /* Connexion RMI aux serveurs */
    private void connecterAuxServeurs() {
        try {
            System.out.println("Connexion aux serveurs...");

            Registry registreAuth = LocateRegistry.getRegistry("localhost", 1099);
            serviceAuth = (IServiceAuthentification) registreAuth.lookup("ServiceAuthentification");
            System.out.println("[OK] Serveur d'authentification");

            Registry registreStockage = LocateRegistry.getRegistry("localhost", 1101);
            serviceStockage = (IServiceStockage) registreStockage.lookup("ServiceStockage");
            System.out.println("[OK] Serveur de stockage\n");

        } catch (Exception e) {
            System.err.println("Impossible de se connecter aux serveurs !");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* Affiche le menu du local */
    private void afficherMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    PART'TOOL - Local #" + idLocal + "              ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.println("\n  1. Déverrouiller le local");
        System.out.println("  2. Afficher informations du local");
        System.out.println("  3. Voir les outils stockés");
        System.out.println("  0. Quitter");
        System.out.print("\nChoix : ");
    }

    /* Déverrouille le local avec carte d'accès */
    private void deverrouillerLocal() {
        System.out.println("\n═══ Déverrouillage du Local ═══");

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
            boolean acces = serviceStockage.deverrouillerLocal(carte, code, idLocal);

            if (acces) {
                System.out.println("\n[OK] Acces autorise ! Bienvenue.");
                connecte = true;
            } else {
                System.err.println("\n[ERREUR] Acces refuse. Verifiez vos identifiants.");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Affiche les informations du local */
    private void afficherInfosLocal() {
        try {
            String jeton = obtenirJetonTemporaire();
            if (jeton == null)
                return;

            LocalStockage local = serviceStockage.obtenirLocal(jeton, idLocal);

            if (local == null) {
                System.err.println("Local introuvable");
                return;
            }

            System.out.println("\n═══ Informations du Local ═══");
            System.out.println(local);

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Affiche les outils stockés dans le local */
    private void afficherOutilsLocal() {
        try {
            String jeton = obtenirJetonTemporaire();
            if (jeton == null)
                return;

            List<Long> outils = serviceStockage.consulterOutilsLocal(jeton, idLocal);

            System.out.println("\n═══ Outils Stockés (" + outils.size() + ") ═══");

            if (outils.isEmpty()) {
                System.out.println("Aucun outil stocké dans ce local");
            } else {
                for (Long qrCode : outils) {
                    System.out.println("  - Outil QR#" + qrCode);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Obtient un jeton temporaire pour les opérations de consultation */
    private String obtenirJetonTemporaire() {
        System.out.print("\nCarte d'accès : ");
        long carte;
        try {
            carte = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Numéro invalide");
            return null;
        }

        System.out.print("Code secret : ");
        String code = scanner.nextLine();

        try {
            var jeton = serviceAuth.seConnecter(carte, code);
            if (jeton == null) {
                System.err.println("Identifiants incorrects");
                return null;
            }
            return jeton.getValeur();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            return null;
        }
    }

    /* Boucle principale */
    public void demarrer() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   PART'TOOL - Local de Stockage #" + idLocal);
        System.out.println("═══════════════════════════════════════════");

        boolean continuer = true;

        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    deverrouillerLocal();
                    break;
                case "2":
                    afficherInfosLocal();
                    break;
                case "3":
                    afficherOutilsLocal();
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
        long idLocal = 1;

        if (args.length > 0) {
            try {
                idLocal = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("ID local invalide, utilisation du local #1");
            }
        }

        ApplicationClientLocal client = new ApplicationClientLocal(idLocal);
        client.demarrer();
    }
}
