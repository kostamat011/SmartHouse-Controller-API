/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmmanager;

import AlarmRequest.AlarmRequest;
import entities.Alarm;
import entities.Reproduction;
import entities.Song;
import entities.User;
import java.util.ArrayList;
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
public class AlarmManager{
    static EntityManager em;
    
    @Resource(lookup="alarmConnectionFactory")
    static ConnectionFactory connFactory;
    
    @Resource(lookup="myAlarmQueue")
    static Queue alarmQueue;
    
    private void saveAlarm(Alarm a){
         em.getTransaction().begin();
        
        /*List<User> users = em.createNamedQuery("User.findByIdUser",User.class).setParameter("idUser",a.getIdUser().getIdUser()).getResultList();
        if(users.size() == 0){
            System.out.println("User was not found.");
            return;
        }
        User user = users.get(0);*/

        em.persist(a);
        em.getTransaction().commit();
    }
    
    
    public AlarmManager(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmManagerPU");
        em=emf.createEntityManager();
    }
    
    public static void main(String[] args) {
        AlarmManager am = new AlarmManager();
        new Thread(new AlarmUpdater()).start();
        JMSContext context = connFactory.createContext();
        JMSConsumer consumer = context.createConsumer(alarmQueue);
        while(true){
            try{
                Message msg = consumer.receive();
                if(!(msg instanceof ObjectMessage)){
                    System.out.println("Bad request");
                    continue;
                }
                ObjectMessage om = (ObjectMessage)msg;
                if(!(om.getObject() instanceof AlarmRequest)){
                    System.out.println("Bad request");
                    continue;
                }
                AlarmRequest ar = (AlarmRequest)om.getObject();
                Alarm a = new Alarm(1);
                a.setIdSong(ar.getIdSong());
                a.setRepeatCount(ar.getRepeatCount());
                a.setRepeatCountTotal(ar.getRepeatCountTotal());
                a.setRepeatPeriod(ar.getRepeatPeriod());
                a.setRepeating(ar.getRepeating());
                a.setStatus(ar.getStatus());
                a.setTime(ar.getTime());
                am.saveAlarm(a);
                
            }catch(Exception ex){
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                break;
            }
        }
    }   
   
}
