package common.modeles;

import java.io.Serializable;
import java.util.Date;

/**
 * Représente un utilisateur de la plateforme PART'TOOL avec ses identifiants
 */
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    private long carteAcces;
    private String code;
    private String nom;
    private String prenom;
    private String email;
    private Date dateInscription;

    public Utilisateur(long carteAcces, String code, String nom, String prenom, String email) {
        this.carteAcces = carteAcces;
        this.code = code;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = new Date();
    }

    public long getCarteAcces() {
        return carteAcces;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "carteAcces=" + carteAcces +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
}
