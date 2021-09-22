/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kosta
 */
@Entity
@Table(name = "reproduction")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reproduction.findAll", query = "SELECT r FROM Reproduction r"),
    @NamedQuery(name = "Reproduction.findByIdReproduction", query = "SELECT r FROM Reproduction r WHERE r.idReproduction = :idReproduction")})
public class Reproduction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idReproduction")
    private Integer idReproduction;
    @JoinColumn(name = "idSong", referencedColumnName = "idSong")
    @ManyToOne(optional = false)
    private Song idSong;
    @JoinColumn(name = "idUser", referencedColumnName = "idUser")
    @ManyToOne(optional = false)
    private User idUser;

    public Reproduction() {
    }

    public Reproduction(Integer idReproduction) {
        this.idReproduction = idReproduction;
    }

    public Integer getIdReproduction() {
        return idReproduction;
    }

    public void setIdReproduction(Integer idReproduction) {
        this.idReproduction = idReproduction;
    }

    public Song getIdSong() {
        return idSong;
    }

    public void setIdSong(Song idSong) {
        this.idSong = idSong;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReproduction != null ? idReproduction.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reproduction)) {
            return false;
        }
        Reproduction other = (Reproduction) object;
        if ((this.idReproduction == null && other.idReproduction != null) || (this.idReproduction != null && !this.idReproduction.equals(other.idReproduction))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Reproduction[ idReproduction=" + idReproduction + " ]";
    }
    
}
