package common.modeles;

import java.io.Serializable;

/**
 * Représente un local de stockage (squelette pour l'étape 3)
 */
public class LocalStockage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idLocal;
    private String adresse;
    private String nomLieu;
    private int capaciteMax;

    public LocalStockage() {
    }
}
