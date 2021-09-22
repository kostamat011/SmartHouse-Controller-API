package entities;

import entities.Alarm;
import entities.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-26T03:04:22")
@StaticMetamodel(Job.class)
public class Job_ { 

    public static volatile SingularAttribute<Job, Integer> duration;
    public static volatile SingularAttribute<Job, Integer> idJob;
    public static volatile SingularAttribute<Job, Alarm> alarmid;
    public static volatile SingularAttribute<Job, String> description;
    public static volatile SingularAttribute<Job, String> location;
    public static volatile SingularAttribute<Job, Date> time;
    public static volatile SingularAttribute<Job, User> userid;

}