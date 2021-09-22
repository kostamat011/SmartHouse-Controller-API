package entities;

import entities.Job;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T03:12:00")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Integer> repeatCountTotal;
    public static volatile SingularAttribute<Alarm, Integer> idSong;
    public static volatile SingularAttribute<Alarm, Short> repeating;
    public static volatile SingularAttribute<Alarm, Integer> repeatPeriod;
    public static volatile SingularAttribute<Alarm, Date> time;
    public static volatile ListAttribute<Alarm, Job> jobList;
    public static volatile SingularAttribute<Alarm, Integer> idAlarm;
    public static volatile SingularAttribute<Alarm, Short> status;
    public static volatile SingularAttribute<Alarm, Integer> repeatCount;

}