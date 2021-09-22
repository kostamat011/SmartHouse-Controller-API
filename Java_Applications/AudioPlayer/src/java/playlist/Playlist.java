/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playlist;

import entities.Song;
import entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kosta
 */
@XmlRootElement(name="putovanjeInfo")
public class Playlist{
    private List<Song> songs;
    private User owner;

    public Playlist(User u){
        owner = u;
        songs = new ArrayList<>();
    }
    
      public Playlist(){
        songs = new ArrayList<>();
    }
    
    public List<Song> getSongs() {
        return songs;
    }

    public void insertSong(Song s){
        songs.add(s);
    }

    public User getOwner() {
        return owner;
    }
    
    public int size(){
        return songs.size();
    }
    
    @XmlElementWrapper(name="Songs")
    @XmlElement(name="song")
    public List<String> getSongNames(){
        List<String> names = new ArrayList<>();
        for(Song s:songs){
            names.add(s.getName());
        }
        return names;
    }
    
}
