package server.outils;

import common.modeles.Emprunt;
import common.modeles.Outil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* Gère la persistance des outils et des emprunts sur disque */
class GestionOutils {

    private static final String FICHIER_OUTILS = "data/outils.dat";
    private static final String FICHIER_EMPRUNTS = "data/emprunts.dat";

    /* Sauvegarde les outils dans un fichier */
    public static void sauvegarder(HashMap<Long, Outil> outils) {
        try {
            File file = new File(FICHIER_OUTILS);
            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(outils);
            }
            System.out.println("[Outils] Sauvegarde : " + outils.size() + " outil(s)");
        } catch (IOException e) {
            System.err.println("[Outils] Erreur sauvegarde : " + e.getMessage());
        }
    }

    /* Charge les outils depuis le fichier */
    @SuppressWarnings("unchecked")
    public static HashMap<Long, Outil> charger() {
        File file = new File(FICHIER_OUTILS);

        if (!file.exists()) {
            System.out.println("[Outils] Aucune donnée existante");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            HashMap<Long, Outil> outils = (HashMap<Long, Outil>) ois.readObject();
            System.out.println("[Outils] Chargement : " + outils.size() + " outil(s)");
            return outils;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Outils] Erreur chargement : " + e.getMessage());
            return new HashMap<>();
        }
    }

    /* Sauvegarde la liste des emprunts dans un fichier */
    public static void sauvegarderEmprunts(List<Emprunt> emprunts) {
        try {
            File file = new File(FICHIER_EMPRUNTS);
            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(emprunts);
            }
            System.out.println("[Outils] Sauvegarde emprunts : " + emprunts.size() + " emprunt(s)");
        } catch (IOException e) {
            System.err.println("[Outils] Erreur sauvegarde emprunts : " + e.getMessage());
        }
    }

    /* Charge la liste des emprunts depuis le fichier */
    @SuppressWarnings("unchecked")
    public static List<Emprunt> chargerEmprunts() {
        File file = new File(FICHIER_EMPRUNTS);

        if (!file.exists()) {
            System.out.println("[Outils] Aucun emprunt enregistré");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Emprunt> emprunts = (List<Emprunt>) ois.readObject();
            System.out.println("[Outils] Chargement emprunts : " + emprunts.size() + " emprunt(s)");
            return emprunts;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Outils] Erreur chargement emprunts : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
