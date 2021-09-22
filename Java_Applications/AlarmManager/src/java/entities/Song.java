/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Kosta
 */
@Entity
@Table(name = "song")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Song.findAll", query = "SELECT s FROM Song s"),
    @NamedQuery(name = "Song.findByIdSong", query = "SELECT s FROM Song s WHERE s.idSong = :idSong"),
    @NamedQuery(name = "Song.findByName", query = "SELECT s FROM Song s WHERE s.name = :name"),
    @NamedQuery(name = "Song.findByUrl", query = "SELECT s FROM Song s WHERE s.url = :url")})
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idSong")
    private Integer idSong;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "url")
    private String url;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSong")
    private List<Reproduction> reproductionList;

    public Song() {
    }

    public Song(Integer idSong) {
        this.idSong = idSong;
    }

    public Song(Integer idSong, String name, String url) {
        this.idSong = idSong;
        this.name = name;
        this.url = url;
    }

    public Integer getIdSong() {
        return idSong;
    }

    public void setIdSong(Integer idSong) {
        this.idSong = idSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlTransient
    public List<Reproduction> getReproductionList() {
        return reproductionList;
    }

    public void setReproductionList(List<Reproduction> reproductionList) {
        this.reproductionList = reproductionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSong != null ? idSong.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Song)) {
            return false;
        }
        Song other = (Song) object;
        if ((this.idSong == null && other.idSong != null) || (this.idSong != null && !this.idSong.equals(other.idSong))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Song[ idSong=" + idSong + " ]";
    }
    
}
