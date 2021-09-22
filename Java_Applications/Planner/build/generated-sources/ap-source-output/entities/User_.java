package entities;

import entities.Job;
import entities.Reproduction;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T03:12:23")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> idUser;
    public static volatile SingularAttribute<User, String> password;
    public static volatile ListAttribute<User, Reproduction> reproductionList;
    public static volatile SingularAttribute<User, String> location;
    public static volatile ListAttribute<User, Job> jobList;
    public static volatile SingularAttribute<User, String> username;

}