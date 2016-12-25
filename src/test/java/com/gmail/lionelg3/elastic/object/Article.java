package com.gmail.lionelg3.elastic.object;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by lionel on 22/11/2016
 *
 */

@XmlRootElement(name = "article", namespace = "blog")
public class Article implements Serializable {

    public static final String FIND_BY_ETAT = "Article.findByEtat";

    public enum Etat {
        EDITION("edition"),
        AFFICHE("affiche"),
        MASQUE("masque"),
        ARCHIVE("archive");
        final String etat;

        Etat(String etat) {
            this.etat = etat;
        }

        String getEtat() {
            return this.etat;
        }
    }

    private String id;

    private Etat etat;

    private long ordre;

    private String titre;

    private Date date;

    private String texte;

    public Article() {
    }

    void prePersist() {
        setOrdre(System.currentTimeMillis());
        setDate(new Date());
        setEtat(Etat.EDITION);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public long getOrdre() {
        return ordre;
    }

    public void setOrdre(long ordre) {
        this.ordre = ordre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (id != null ? !id.equals(article.id) : article.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", etat=" + etat +
                ", ordre=" + ordre +
                ", titre='" + titre + '\'' +
                ", date=" + date +
                ", texte='" + texte + '\'' +
                '}';
    }
}
