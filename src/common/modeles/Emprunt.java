package common.modeles;

import java.io.Serializable;
import java.util.Date;

/* Représente une transaction d'emprunt d'outil */
public class Emprunt implements Serializable {

    private static final long serialVersionUID = 1L;

    private long qrCode;
    private long carteEmprunteur;
    private long idLocal;
    private Date dateEmprunt;
    private Date dateRestitution;

    public Emprunt(long qrCode, long carteEmprunteur, long idLocal) {
        this.qrCode = qrCode;
        this.carteEmprunteur = carteEmprunteur;
        this.idLocal = idLocal;
        this.dateEmprunt = new Date();
        this.dateRestitution = null;
    }

    public long getQrCode() {
        return qrCode;
    }

    public long getCarteEmprunteur() {
        return carteEmprunteur;
    }

    public long getIdLocal() {
        return idLocal;
    }

    public Date getDateEmprunt() {
        return dateEmprunt;
    }

    public Date getDateRestitution() {
        return dateRestitution;
    }

    public boolean estEnCours() {
        return dateRestitution == null;
    }

    public void restituer() {
        this.dateRestitution = new Date();
    }

    @Override
    public String toString() {
        return String.format(
                "┌─ Emprunt Outil QR#%d ──────────────────────\n" +
                        "│ Emprunteur (carte) : %d\n" +
                        "│ Local de départ    : #%d\n" +
                        "│ Date d'emprunt     : %s\n" +
                        "│ Restitué le        : %s\n" +
                        "└──────────────────────────────────────────",
                qrCode, carteEmprunteur, idLocal,
                dateEmprunt,
                dateRestitution != null ? dateRestitution.toString() : "En cours");
    }
}
