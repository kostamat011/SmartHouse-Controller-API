package entities;

import entities.Reproduction;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T03:12:08")
@StaticMetamodel(Song.class)
public class Song_ { 

    public static volatile SingularAttribute<Song, Integer> idSong;
    public static volatile ListAttribute<Song, Reproduction> reproductionList;
    public static volatile SingularAttribute<Song, String> name;
    public static volatile SingularAttribute<Song, String> url;

}