package common.modeles;

import java.io.Serializable;

/**
 * Représente un outil (squelette pour l'étape 2)
 */
public class Outil implements Serializable {

    private static final long serialVersionUID = 1L;

    private long qrCode;
    private String nom;
    private String usage;
    private String description;
    private double poids;
    private String dimensions;
    private long proprietaire;
    private boolean estEmprunte;
    private long empruntePar;

    public Outil() {
    }
}
