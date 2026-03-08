package client;

import common.IServiceStockage;
import common.IServiceAuthentification;
import common.IServiceOutils;
import common.modeles.Jeton;
import common.modeles.LocalStockage;
import common.modeles.Outil;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/* Client pour accès à un local de stockage spécifique.
   Permet de déverrouiller le local et d'enregistrer les emprunts/restitutions par QR code. */
public class ApplicationClientLocal {

    private IServiceStockage serviceStockage;
    private IServiceAuthentification serviceAuth;
    private IServiceOutils serviceOutils;
    private Scanner scanner;
    private long idLocal;
    private String jetonCourant;

    public ApplicationClientLocal(long idLocal) {
        this.idLocal = idLocal;
        this.scanner = new Scanner(System.in);
        this.jetonCourant = null;
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
            System.out.println("[OK] Serveur de stockage");

            Registry registreOutils = LocateRegistry.getRegistry("localhost", 1100);
            serviceOutils = (IServiceOutils) registreOutils.lookup("ServiceOutils");
            System.out.println("[OK] Serveur d'outils\n");

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

        if (jetonCourant != null) {
            System.out.println("[ Session ouverte ]");
        }

        System.out.println("\n  1. Déverrouiller le local");
        System.out.println("  2. Afficher informations du local");
        System.out.println("  3. Voir les outils stockés");

        if (jetonCourant != null) {
            System.out.println("  4. Emprunter un outil (scan QR code)");
            System.out.println("  5. Restituer un outil (scan QR code)");
        }

        System.out.println("  0. Quitter");
        System.out.print("\nChoix : ");
    }

    /* Déverrouille le local avec carte d'accès et conserve le jeton */
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
                /* Connecte l'utilisateur et conserve le jeton pour les actions suivantes */
                Jeton jeton = serviceAuth.seConnecter(carte, code);
                if (jeton != null) {
                    jetonCourant = jeton.getValeur();
                }
                System.out.println("\n[OK] Acces autorise ! Bienvenue.");
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
            String jeton = obtenirJeton();
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

    /* Affiche les outils stockés dans le local avec leurs informations */
    private void afficherOutilsLocal() {
        try {
            String jeton = obtenirJeton();
            if (jeton == null)
                return;

            List<Long> qrCodes = serviceStockage.consulterOutilsLocal(jeton, idLocal);

            System.out.println("\n═══ Outils Stockés (" + qrCodes.size() + ") ═══");

            if (qrCodes.isEmpty()) {
                System.out.println("Aucun outil stocké dans ce local");
            } else {
                for (Long qrCode : qrCodes) {
                    try {
                        Outil outil = serviceOutils.obtenirOutil(jeton, qrCode);
                        if (outil != null) {
                            System.out.println("  - QR#" + qrCode + " | " + outil.getNom() +
                                    " [" + outil.getEtat().getLibelle() + "]");
                        } else {
                            System.out.println("  - QR#" + qrCode);
                        }
                    } catch (Exception e) {
                        System.out.println("  - QR#" + qrCode);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Enregistre l'emprunt d'un outil par scan du QR code */
    private void emprunterOutil() {
        if (jetonCourant == null) {
            System.err.println("Déverrouillez le local d'abord (option 1)");
            return;
        }

        System.out.println("\n═══ Emprunt d'Outil ═══");
        System.out.print("QR code de l'outil : ");

        long qrCode;
        try {
            qrCode = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("QR code invalide");
            return;
        }

        try {
            /* Affiche d'abord les informations de l'outil */
            Outil outil = serviceOutils.obtenirOutil(jetonCourant, qrCode);
            if (outil == null) {
                System.err.println("Outil QR#" + qrCode + " introuvable");
                return;
            }

            System.out.println("\nOutil trouvé : " + outil.getNom() + " [" + outil.getEtat().getLibelle() + "]");
            System.out.print("Confirmer l'emprunt ? (o/n) : ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("o")) {
                System.out.println("Emprunt annulé");
                return;
            }

            boolean succes = serviceOutils.emprunterOutil(jetonCourant, qrCode, idLocal);

            if (succes) {
                System.out.println("\n[OK] Emprunt enregistré ! Bon usage.");
                System.out.println("     Outil : " + outil.getNom() + " (QR#" + qrCode + ")");
            } else {
                System.err.println("[ERREUR] Emprunt refusé.");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Enregistre la restitution d'un outil par scan du QR code */
    private void restituerOutil() {
        if (jetonCourant == null) {
            System.err.println("Déverrouillez le local d'abord (option 1)");
            return;
        }

        System.out.println("\n═══ Restitution d'Outil ═══");
        System.out.print("QR code de l'outil : ");

        long qrCode;
        try {
            qrCode = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("QR code invalide");
            return;
        }

        try {
            /* Affiche les informations de l'outil */
            Outil outil = serviceOutils.obtenirOutil(jetonCourant, qrCode);
            if (outil == null) {
                System.err.println("Outil QR#" + qrCode + " introuvable");
                return;
            }

            System.out.println("\nOutil trouvé : " + outil.getNom() + " [" + outil.getEtat().getLibelle() + "]");
            System.out.print("Confirmer la restitution dans le local #" + idLocal + " ? (o/n) : ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("o")) {
                System.out.println("Restitution annulée");
                return;
            }

            boolean succes = serviceOutils.restituerOutil(jetonCourant, qrCode, idLocal);

            if (succes) {
                System.out.println("\n[OK] Restitution enregistrée ! Merci.");
                System.out.println("     Outil : " + outil.getNom() + " (QR#" + qrCode + ") de retour au local #" + idLocal);
            } else {
                System.err.println("[ERREUR] Restitution refusée.");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /* Retourne le jeton courant (ou demande une connexion temporaire si absent) */
    private String obtenirJeton() {
        if (jetonCourant != null) {
            return jetonCourant;
        }
        /* Connexion temporaire pour la consultation */
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
                case "4":
                    emprunterOutil();
                    break;
                case "5":
                    restituerOutil();
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
