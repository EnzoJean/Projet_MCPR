package server.authentification;

import common.modeles.Utilisateur;
import java.io.*;
import java.util.HashMap;

/* Gère la persistence des utilisateurs sur disque */
public class GestionUtilisateurs {

    private static final String FICHIER_UTILISATEURS = "data/utilisateurs.dat";

    /* Sauvegarde tous les utilisateurs dans un fichier  */
    public static void sauvegarder(HashMap<Long, Utilisateur> utilisateurs) {
        try {
            File fichier = new File(FICHIER_UTILISATEURS);
            fichier.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
                oos.writeObject(utilisateurs);
                System.out.println("[Persistence] " + utilisateurs.size() + " utilisateurs sauvegardés");
            }
        } catch (IOException e) {
            System.err.println("[Persistence] Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /* Charge tous les utilisateurs depuis le fichier */
    @SuppressWarnings("unchecked")
    public static HashMap<Long, Utilisateur> charger() {
        File fichier = new File(FICHIER_UTILISATEURS);

        if (!fichier.exists()) {
            System.out.println("[Persistence] Aucune donnée existante, création d'une base vide");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            HashMap<Long, Utilisateur> utilisateurs = (HashMap<Long, Utilisateur>) ois.readObject();
            System.out.println(
                    "[Persistence] " + utilisateurs.size() + " utilisateurs chargés depuis " + FICHIER_UTILISATEURS);
            return utilisateurs;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Persistence] Erreur lors du chargement : " + e.getMessage());
            return new HashMap<>();
        }
    }
}
