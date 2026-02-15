package server.stockage;

import common.modeles.LocalStockage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/* Gère la persistance des locaux sur disque */
class GestionLocaux {

    private static final String FICHIER_LOCAUX = "data/locaux.dat";

    /* Sauvegarde les locaux dans un fichier */
    public static void sauvegarder(Map<Long, LocalStockage> locaux) {
        try {
            File file = new File(FICHIER_LOCAUX);
            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(locaux);
            }
            System.out.println("[Stockage] Sauvegarde : " + locaux.size() + " local/locaux");
        } catch (IOException e) {
            System.err.println("[Stockage] Erreur sauvegarde : " + e.getMessage());
        }
    }

    /* Charge les locaux depuis le fichier */
    @SuppressWarnings("unchecked")
    public static Map<Long, LocalStockage> charger() {
        File file = new File(FICHIER_LOCAUX);

        if (!file.exists()) {
            System.out.println("[Stockage] Aucune donnée existante, création des locaux par défaut");
            return initialiserLocauxParDefaut();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<Long, LocalStockage> locaux = (Map<Long, LocalStockage>) ois.readObject();
            System.out.println("[Stockage] Chargement : " + locaux.size() + " local/locaux");
            return locaux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Stockage] Erreur chargement : " + e.getMessage());
            return initialiserLocauxParDefaut();
        }
    }

    /* Initialise quelques locaux par défaut */
    private static Map<Long, LocalStockage> initialiserLocauxParDefaut() {
        Map<Long, LocalStockage> locaux = new HashMap<>();

        LocalStockage local1 = new LocalStockage(1, "Universite Toulouse III - Paul Sabatier",
                "118 Route de Narbonne", "Toulouse", "31062", 50);
        locaux.put(1L, local1);

        LocalStockage local2 = new LocalStockage(2, "Universite Toulouse I - Capitole",
                "2 Rue du Doyen Gabriel Marty", "Toulouse", "31042", 30);
        locaux.put(2L, local2);

        LocalStockage local3 = new LocalStockage(3, "Universite Toulouse II - Jean Jaures",
                "5 Allee Antonio Machado", "Toulouse", "31058", 40);
        locaux.put(3L, local3);

        System.out.println("[Stockage] " + locaux.size() + " locaux par defaut crees");
        sauvegarder(locaux);

        return locaux;
    }
}
