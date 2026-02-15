package common.modeles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Représente un local de stockage */
public class LocalStockage implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String nom;
    private String adresse;
    private String ville;
    private String codePostal;
    private int capaciteMax;
    private int nbOutilsStockes;
    private StatutLocal statut;
    private List<Long> utilisateursAutorises;
    private List<Long> outilsStockes;
    private Date dateCreation;

    public LocalStockage(long id, String nom, String adresse, String ville, String codePostal, int capaciteMax) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.capaciteMax = capaciteMax;
        this.nbOutilsStockes = 0;
        this.statut = StatutLocal.OUVERT;
        this.utilisateursAutorises = new ArrayList<>();
        this.outilsStockes = new ArrayList<>();
        this.dateCreation = new Date();
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public int getNbOutilsStockes() {
        return nbOutilsStockes;
    }

    public StatutLocal getStatut() {
        return statut;
    }

    public List<Long> getUtilisateursAutorises() {
        return utilisateursAutorises;
    }

    public List<Long> getOutilsStockes() {
        return outilsStockes;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setStatut(StatutLocal statut) {
        this.statut = statut;
    }

    public void setNbOutilsStockes(int nbOutilsStockes) {
        this.nbOutilsStockes = nbOutilsStockes;
    }

    public void ajouterUtilisateurAutorise(long carteAcces) {
        if (!utilisateursAutorises.contains(carteAcces)) {
            utilisateursAutorises.add(carteAcces);
        }
    }

    public boolean estAutorise(long carteAcces) {
        return utilisateursAutorises.isEmpty() || utilisateursAutorises.contains(carteAcces);
    }

    public void ajouterOutil(long qrCode) {
        if (!outilsStockes.contains(qrCode)) {
            outilsStockes.add(qrCode);
            nbOutilsStockes = outilsStockes.size();
        }
    }

    public void retirerOutil(long qrCode) {
        outilsStockes.remove(qrCode);
        nbOutilsStockes = outilsStockes.size();
    }

    @Override
    public String toString() {
        return String.format(
                "┌─ Local #%d ─────────────────────────────\n" +
                        "│ Nom: %s\n" +
                        "│ Adresse: %s\n" +
                        "│ Ville: %s %s\n" +
                        "│ Capacité: %d/%d outils\n" +
                        "│ Statut: %s\n" +
                        "│ Utilisateurs autorisés: %d\n" +
                        "└──────────────────────────────────────────",
                id, nom, adresse, codePostal, ville,
                nbOutilsStockes, capaciteMax, statut.getLibelle(),
                utilisateursAutorises.size());
    }
}
