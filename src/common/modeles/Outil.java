package common.modeles;

import java.io.Serializable;
import java.util.Date;

/* Représente un outil disponible sur la plateforme */
public class Outil implements Serializable {

    private static final long serialVersionUID = 1L;

    private long qrCode;
    private String nom;
    private String usage;
    private String description;
    private double poids;
    private String dimensions;
    private long proprietaire;
    private CategorieOutil categorie;
    private EtatOutil etat;
    private Long localStockageId;
    private Long empruntePar;
    private Date dateDeclaration;

    public Outil(long qrCode, String nom, String usage, String description, double poids,
            String dimensions, long proprietaire, CategorieOutil categorie) {
        this.qrCode = qrCode;
        this.nom = nom;
        this.usage = usage;
        this.description = description;
        this.poids = poids;
        this.dimensions = dimensions;
        this.proprietaire = proprietaire;
        this.categorie = categorie;
        this.etat = EtatOutil.DISPONIBLE;
        this.localStockageId = null;
        this.empruntePar = null;
        this.dateDeclaration = new Date();
    }

    public long getQrCode() {
        return qrCode;
    }

    public String getNom() {
        return nom;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public double getPoids() {
        return poids;
    }

    public String getDimensions() {
        return dimensions;
    }

    public long getProprietaire() {
        return proprietaire;
    }

    public CategorieOutil getCategorie() {
        return categorie;
    }

    public EtatOutil getEtat() {
        return etat;
    }

    public Long getLocalStockageId() {
        return localStockageId;
    }

    public Long getEmpruntePar() {
        return empruntePar;
    }

    public Date getDateDeclaration() {
        return dateDeclaration;
    }

    public void setEtat(EtatOutil etat) {
        this.etat = etat;
    }

    public void setLocalStockageId(Long localStockageId) {
        this.localStockageId = localStockageId;
    }

    public void setEmpruntePar(Long empruntePar) {
        this.empruntePar = empruntePar;
    }

    @Override
    public String toString() {
        return String.format(
                "┌─ Outil QR#%d ─────────────────────────────\n" +
                        "│ Nom: %s\n" +
                        "│ Usage: %s\n" +
                        "│ Description: %s\n" +
                        "│ Poids: %.2f kg | Dimensions: %s\n" +
                        "│ Catégorie: %s\n" +
                        "│ État: %s\n" +
                        "│ Local: %s\n" +
                        "└──────────────────────────────────────────",
                qrCode, nom, usage, description, poids, dimensions,
                categorie.getLibelle(), etat.getLibelle(),
                localStockageId != null ? "Local #" + localStockageId : "Non stocké");
    }
}
