package common.modeles;

import java.io.Serializable;
import java.util.Date;

/**
 * Représente un jeton de session avec une durée de validité
 */
public class Jeton implements Serializable {

    private static final long serialVersionUID = 1L;

    private String valeur;
    private long carteAcces;
    private Date dateCreation;
    private Date dateExpiration;

    public Jeton(String valeur, long carteAcces, Date dateExpiration) {
        this.valeur = valeur;
        this.carteAcces = carteAcces;
        this.dateCreation = new Date();
        this.dateExpiration = dateExpiration;
    }

    public String getValeur() {
        return valeur;
    }

    public long getCarteAcces() {
        return carteAcces;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    /**
     * Vérifie si le jeton n'est pas encore expiré
     */
    public boolean estValide() {
        Date maintenant = new Date();
        return maintenant.before(dateExpiration);
    }

    @Override
    public String toString() {
        return "Jeton{" +
                "valeur='" + valeur + '\'' +
                ", carteAcces=" + carteAcces +
                ", dateCreation=" + dateCreation +
                ", dateExpiration=" + dateExpiration +
                ", valide=" + estValide() +
                '}';
    }
}
