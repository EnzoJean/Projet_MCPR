package common.modeles;

import java.io.Serializable;
import java.util.Map;

/* Statistiques du serveur de stockage, transportées via RMI */
public class StatistiquesStockage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int nbLocauxTotal;
    private int nbOutilsStockes;
    private int capaciteTotale;
    private double tauxOccupation;
    private Map<Long, Integer> nbOutilsParLocal;
    private Map<Long, Integer> nbRestitutionsParLocal;

    public StatistiquesStockage(int nbLocauxTotal, int nbOutilsStockes, int capaciteTotale,
                                 double tauxOccupation,
                                 Map<Long, Integer> nbOutilsParLocal,
                                 Map<Long, Integer> nbRestitutionsParLocal) {
        this.nbLocauxTotal = nbLocauxTotal;
        this.nbOutilsStockes = nbOutilsStockes;
        this.capaciteTotale = capaciteTotale;
        this.tauxOccupation = tauxOccupation;
        this.nbOutilsParLocal = nbOutilsParLocal;
        this.nbRestitutionsParLocal = nbRestitutionsParLocal;
    }

    public int getNbLocauxTotal() { return nbLocauxTotal; }
    public int getNbOutilsStockes() { return nbOutilsStockes; }
    public int getCapaciteTotale() { return capaciteTotale; }
    public double getTauxOccupation() { return tauxOccupation; }
    public Map<Long, Integer> getNbOutilsParLocal() { return nbOutilsParLocal; }
    public Map<Long, Integer> getNbRestitutionsParLocal() { return nbRestitutionsParLocal; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─ Statistiques Stockage ─────────────────────\n");
        sb.append(String.format("│ Locaux             : %d\n", nbLocauxTotal));
        sb.append(String.format("│ Outils stockés     : %d / %d\n", nbOutilsStockes, capaciteTotale));
        sb.append(String.format("│ Taux d'occupation  : %.1f%%\n", tauxOccupation));

        if (!nbOutilsParLocal.isEmpty()) {
            sb.append("│\n│ Outils par local :\n");
            nbOutilsParLocal.forEach((idLocal, nb) -> {
                int restitutions = nbRestitutionsParLocal.getOrDefault(idLocal, 0);
                sb.append(String.format("│   Local #%-5d : %d outil(s) stocké(s), %d restitution(s)\n",
                        idLocal, nb, restitutions));
            });
        }

        sb.append("└──────────────────────────────────────────────");
        return sb.toString();
    }
}
