package common.modeles;

import java.io.Serializable;
import java.util.Map;

/* Statistiques du serveur de gestion des outils, transportées via RMI */
public class StatistiquesOutils implements Serializable {

    private static final long serialVersionUID = 1L;

    private int nbOutilsTotal;
    private int nbOutilsDisponibles;
    private int nbOutilsEmpruntes;
    private int nbEmpruntsTotal;
    private int nbEmpruntsEnCours;
    private Map<String, Integer> nbOutilsParCategorie;
    private Map<Long, Integer> nbEmpruntsParUtilisateur;

    public StatistiquesOutils(int nbOutilsTotal, int nbOutilsDisponibles, int nbOutilsEmpruntes,
                               int nbEmpruntsTotal, int nbEmpruntsEnCours,
                               Map<String, Integer> nbOutilsParCategorie,
                               Map<Long, Integer> nbEmpruntsParUtilisateur) {
        this.nbOutilsTotal = nbOutilsTotal;
        this.nbOutilsDisponibles = nbOutilsDisponibles;
        this.nbOutilsEmpruntes = nbOutilsEmpruntes;
        this.nbEmpruntsTotal = nbEmpruntsTotal;
        this.nbEmpruntsEnCours = nbEmpruntsEnCours;
        this.nbOutilsParCategorie = nbOutilsParCategorie;
        this.nbEmpruntsParUtilisateur = nbEmpruntsParUtilisateur;
    }

    public int getNbOutilsTotal() { return nbOutilsTotal; }
    public int getNbOutilsDisponibles() { return nbOutilsDisponibles; }
    public int getNbOutilsEmpruntes() { return nbOutilsEmpruntes; }
    public int getNbEmpruntsTotal() { return nbEmpruntsTotal; }
    public int getNbEmpruntsEnCours() { return nbEmpruntsEnCours; }
    public Map<String, Integer> getNbOutilsParCategorie() { return nbOutilsParCategorie; }
    public Map<Long, Integer> getNbEmpruntsParUtilisateur() { return nbEmpruntsParUtilisateur; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─ Statistiques Outils ───────────────────────\n");
        sb.append(String.format("│ Outils déclarés    : %d\n", nbOutilsTotal));
        sb.append(String.format("│ Disponibles        : %d\n", nbOutilsDisponibles));
        sb.append(String.format("│ Empruntés          : %d\n", nbOutilsEmpruntes));
        sb.append(String.format("│ Emprunts total     : %d\n", nbEmpruntsTotal));
        sb.append(String.format("│ Emprunts en cours  : %d\n", nbEmpruntsEnCours));

        if (!nbOutilsParCategorie.isEmpty()) {
            sb.append("│\n│ Répartition par catégorie :\n");
            nbOutilsParCategorie.forEach((cat, nb) ->
                    sb.append(String.format("│   %-25s : %d\n", cat, nb)));
        }

        if (!nbEmpruntsParUtilisateur.isEmpty()) {
            sb.append("│\n│ Emprunts par utilisateur (carte) :\n");
            nbEmpruntsParUtilisateur.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(e -> sb.append(String.format("│   Carte %-15d : %d emprunt(s)\n",
                            e.getKey(), e.getValue())));
        }

        sb.append("└──────────────────────────────────────────────");
        return sb.toString();
    }
}
