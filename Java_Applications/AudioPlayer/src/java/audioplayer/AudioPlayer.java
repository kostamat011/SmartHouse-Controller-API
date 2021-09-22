/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer;

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
import playlist.Playlist;
import youtube.YTBrowser;

import youtube.YTBrowser;

/**
 *
 * @author Kosta
 */
public class AudioPlayer {
    static EntityManager em;
    
    @Resource(lookup="playbackConnectionFactory")
    static ConnectionFactory connFactory;
    
    @Resource(lookup="songQueue")
    static Queue songQueue;
    
    @Resource(lookup="myPlaylistTopic")
    static Topic playlistTopic;
    
    static YTBrowser yt;
    
    public AudioPlayer(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AudioPlayerPU");
        em=emf.createEntityManager();
    }
    
    private void returnPlaylist(int idUser){
        JMSContext context = connFactory.createContext();
        JMSProducer producer = context.createProducer();
            
        List<User> users = em.createNamedQuery("User.findByIdUser",User.class).setParameter("idUser",idUser).getResultList();
        if(users.size() == 0){
            System.out.println("User was not found.");
            return;
        }
        User user = users.get(0);
            
            
        ArrayList<String> titles = new ArrayList<>();
        List<Reproduction> reproductions = em.createQuery("SELECT r FROM Reproduction r WHERE r.idUser=:idUser",Reproduction.class).setParameter("idUser", user).getResultList();
        for(Reproduction r:reproductions){
            titles.add(r.getIdSong().getName());
        }
            
        if(titles.size()==0){
            System.out.println("This user has no songs in playlist");
            return;
        }
            
        ObjectMessage objectMessage = context.createObjectMessage(titles);
        try {
            objectMessage.setIntProperty("idUser", idUser);
        } catch (JMSException ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("sending object message");
        producer.send(playlistTopic, objectMessage);
    }
    
    private void playSong(String songName, int idUser){
        String songURL = YTBrowser.runBrowser(songName);
        if(songURL==null){
            System.out.println("Song was not found.");
            return;
        }
        
        Song song = null;
        
        em.getTransaction().begin();
        
        List<User> users = em.createNamedQuery("User.findByIdUser",User.class).setParameter("idUser",idUser).getResultList();
        if(users.size() == 0){
            System.out.println("User was not found.");
            return;
        }
        User user = users.get(0);
        
        List<Song> songs = em.createNamedQuery("Song.findByName",Song.class).setParameter("name", songName).getResultList();
        if(songs.size()==0){
            System.out.println("New song in database.");
            song = new Song();
            song.setName(songName);
            song.setUrl(songURL);
            em.persist(song);
        }else{
            song = songs.get(0);
        }
        Reproduction reproduction = new Reproduction();
        reproduction.setIdSong(song);
        reproduction.setIdUser(user);
        
        user.getReproductionList().add(reproduction);
        em.persist(reproduction);
        
        em.getTransaction().commit();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AudioPlayer pbd = new AudioPlayer();
        
        JMSContext context = connFactory.createContext();
        JMSConsumer consumer = context.createConsumer(songQueue);
        while(true){
            try{
                Message msg = consumer.receive();
                if(!(msg instanceof TextMessage)){
                    System.out.println("Bad request");
                    continue;
                }
                String text = ((TextMessage)msg).getText();
                System.out.println(text);
                String[] tokens = text.split("#");
                
                // request for playlist
                if(tokens[0].equals("playlist")){
                    pbd.returnPlaylist(Integer.parseInt(tokens[1]));
                }
                
                // request for playing song
                else{
                    pbd.playSong(tokens[0], Integer.parseInt(tokens[1]));
                }
            }catch(Exception ex){
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                break;
            }
        }
    }
    
}
