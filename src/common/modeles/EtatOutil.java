package common.modeles;

import java.io.Serializable;

/* États possibles d'un outil */
public enum EtatOutil implements Serializable {
    DISPONIBLE("Disponible"),
    EMPRUNTE("Emprunté"),
    EN_MAINTENANCE("En maintenance"),
    RETIRE("Retiré");

    private final String libelle;

    EtatOutil(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
