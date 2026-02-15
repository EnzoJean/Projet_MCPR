package common.modeles;

import java.io.Serializable;

/* Catégories d'outils disponibles */
public enum CategorieOutil implements Serializable {
    BRICOLAGE("Bricolage"),
    JARDINAGE("Jardinage"),
    ELECTROMENAGER("Électroménager"),
    MACONNERIE("Maçonnerie"),
    PLOMBERIE("Plomberie"),
    ELECTRICITE("Électricité"),
    PEINTURE("Peinture"),
    AUTOMOBILE("Automobile"),
    AUTRE("Autre");

    private final String libelle;

    CategorieOutil(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
