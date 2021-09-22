/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlarmRequest;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Kosta
 */
public class AlarmRequest implements Serializable {
    private Integer idAlarm;
    private Short repeating;
    private Integer repeatPeriod;
    private Date time;
    private Integer idSong;
    private Short status;
    private Integer repeatCount;
    private Integer repeatCountTotal;

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
    
    
}
