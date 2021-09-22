package entities;

import entities.Song;
import entities.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T03:12:23")
@StaticMetamodel(Reproduction.class)
public class Reproduction_ { 

    public static volatile SingularAttribute<Reproduction, User> idUser;
    public static volatile SingularAttribute<Reproduction, Integer> idReproduction;
    public static volatile SingularAttribute<Reproduction, Song> idSong;

}