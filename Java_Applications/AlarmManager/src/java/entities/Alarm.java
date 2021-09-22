/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Kosta
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdAlarm", query = "SELECT a FROM Alarm a WHERE a.idAlarm = :idAlarm"),
    @NamedQuery(name = "Alarm.findByRepeating", query = "SELECT a FROM Alarm a WHERE a.repeating = :repeating"),
    @NamedQuery(name = "Alarm.findByRepeatPeriod", query = "SELECT a FROM Alarm a WHERE a.repeatPeriod = :repeatPeriod"),
    @NamedQuery(name = "Alarm.findByTime", query = "SELECT a FROM Alarm a WHERE a.time = :time"),
    @NamedQuery(name = "Alarm.findByIdSong", query = "SELECT a FROM Alarm a WHERE a.idSong = :idSong"),
    @NamedQuery(name = "Alarm.findByStatus", query = "SELECT a FROM Alarm a WHERE a.status = :status"),
    @NamedQuery(name = "Alarm.findByRepeatCount", query = "SELECT a FROM Alarm a WHERE a.repeatCount = :repeatCount"),
    @NamedQuery(name = "Alarm.findByRepeatCountTotal", query = "SELECT a FROM Alarm a WHERE a.repeatCountTotal = :repeatCountTotal")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAlarm")
    private Integer idAlarm;
    @Column(name = "repeating")
    private Short repeating;
    @Column(name = "repeatPeriod")
    private Integer repeatPeriod;
    @Basic(optional = false)
    @NotNull
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "idSong")
    private Integer idSong;
    @Column(name = "status")
    private Short status;
    @Column(name = "repeatCount")
    private Integer repeatCount;
    @Column(name = "repeatCountTotal")
    private Integer repeatCountTotal;
    @OneToMany(mappedBy = "alarmid")
    private List<Job> jobList;

    public Alarm() {
    }

    public Alarm(Integer idAlarm) {
        this.idAlarm = idAlarm;
    }

    public Alarm(Integer idAlarm, Date time) {
        this.idAlarm = idAlarm;
        this.time = time;
    }

    public Integer getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(Integer idAlarm) {
        this.idAlarm = idAlarm;
    }

    public Short getRepeating() {
        return repeating;
    }

    public void setRepeating(Short repeating) {
        this.repeating = repeating;
    }

    public Integer getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(Integer repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getIdSong() {
        return idSong;
    }

    public void setIdSong(Integer idSong) {
        this.idSong = idSong;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Integer getRepeatCountTotal() {
        return repeatCountTotal;
    }

    public void setRepeatCountTotal(Integer repeatCountTotal) {
        this.repeatCountTotal = repeatCountTotal;
    }

    @XmlTransient
    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAlarm != null ? idAlarm.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idAlarm == null && other.idAlarm != null) || (this.idAlarm != null && !this.idAlarm.equals(other.idAlarm))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Alarm[ idAlarm=" + idAlarm + " ]";
    }
    
}
