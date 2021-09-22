/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmmanager;

import static alarmmanager.AlarmManager.em;
import static datetime.TimeCalc.isSameTime;
import entities.Alarm;
import entities.Reproduction;
import entities.Song;
import entities.User;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import static java.lang.System.in;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import youtube.YTBrowser;

import youtube.YTBrowser;

/**
 *
 * @author Kosta
 */
public class AlarmUpdater implements Runnable {

    static EntityManager em;
    static YTBrowser yt;

    

    public AlarmUpdater() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmManagerPU");
        em = emf.createEntityManager();
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Alarm> alarms = em.createQuery("SELECT a FROM Alarm a", Alarm.class).getResultList();
                if (alarms != null) {
                    LocalDateTime now = LocalDateTime.now();
                    for (Alarm alarm : alarms) {

                        // non repeating alarm
                        // turn off after ringing
                        if ((alarm.getRepeating() == 0) && (alarm.getStatus() == 1)) {
                            LocalDateTime alarmTime = LocalDateTime.ofInstant(alarm.getTime().toInstant(), ZoneId.systemDefault());
                            if (isSameTime(alarmTime, now)) {
                                if (alarm.getIdSong()==0) {
                                    File defAlarm = new File("default_alarm.mp3");
                                    Desktop.getDesktop().browse(defAlarm.toURI());
                                } else {
                                    Song song = em.createNamedQuery("Song.findByIdSong",Song.class).setParameter("idSong", alarm.getIdSong()).getSingleResult();
                                    YTBrowser.runBrowser(song.getName());
                                }

                                alarm.setStatus(new Integer(0).shortValue());
                                em.getTransaction().begin();
                                em.merge(alarm);
                                em.getTransaction().commit();
                            }
                        } 
                        
                        // repeating alarm
                        // increase repeat count
                        // if repeat count reached total repeat count turn off
                        else {
                            LocalDateTime alarmTime = LocalDateTime.ofInstant(alarm.getTime().toInstant(), ZoneId.systemDefault());
                            LocalDateTime repeatTime = now.minusSeconds(60 * alarm.getRepeatPeriod() * alarm.getRepeatCount());
                            if (isSameTime(alarmTime,repeatTime) && (alarm.getStatus() == 1)) {
                                if (alarm.getIdSong()==0) {
                                    File defAlarm = new File("default_alarm.mp3");
                                    Desktop.getDesktop().browse(defAlarm.toURI());
                                } else {
                                    Song song = em.createNamedQuery("Song.findByIdSong",Song.class).setParameter("idSong", alarm.getIdSong()).getSingleResult();
                                    YTBrowser.runBrowser(song.getName());
                                }
                                alarm.setRepeatCount(alarm.getRepeatCount() + 1);
                                if (alarm.getRepeatCount() >= alarm.getRepeatCountTotal()) {
                                    alarm.setStatus(new Integer(0).shortValue());
                                }
                                em.getTransaction().begin();
                                em.merge(alarm);
                                em.getTransaction().commit();
                            }
                        }
                    }
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
