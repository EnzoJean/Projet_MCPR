package common.modeles;

/* Statuts possibles d'un local de stockage */
public enum StatutLocal {
    OUVERT("Ouvert"),
    FERME("Fermé"),
    EN_MAINTENANCE("En maintenance"),
    HORS_SERVICE("Hors service");

    private final String libelle;

    StatutLocal(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
