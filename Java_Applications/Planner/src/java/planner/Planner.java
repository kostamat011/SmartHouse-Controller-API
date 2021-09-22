/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import JobRequest.JobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Alarm;
import entities.Job;
import entities.Reproduction;
import entities.Song;
import entities.User;
import geography.Position;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kosta
 */
public class Planner {

    static EntityManager em;
    private static final String apiKey = "6nOgyjX2B5bQtpHvP7mjEbgHe1dYSj7JgLVBwUY5tZw";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Resource(lookup = "jobConnectionFactory")
    static ConnectionFactory connFactory;

    @Resource(lookup = "myJobQueue")
    static Queue jobQueue;

    @Resource(lookup = "myJobListQueue")
    static Queue jobListQueue;

    private static Job previousJob(Job j) {
        List<Job> userJobs = em.createQuery("SELECT j FROM Job j WHERE j.userid=:id", Job.class)
                .setParameter("id", j.getUserid())
                .getResultList();

        Job prev = null;
        for (Job job : userJobs) {
            if ((prev == null) && job.getTime().before(j.getTime())) {
                prev = job;
            } else if ((prev != null) && job.getTime().after(prev.getTime()) && job.getTime().before(j.getTime())) {
                prev = job;
            }
        }

        return prev;
    }

    private static Job nextJob(Job j) {
        List<Job> userJobs = em.createQuery("SELECT j FROM Job j WHERE j.userid=:id", Job.class)
                .setParameter("id", j.getUserid())
                .getResultList();

        Job next = null;
        for (Job job : userJobs) {
            if ((next == null) && job.getTime().after(j.getTime())) {
                next = job;
            } else if ((next != null) && job.getTime().before(next.getTime()) && job.getTime().after(j.getTime())) {
                next = job;
            }
        }

        return next;
    }

    private static boolean canFitJob(Job j) {
        Job prev = previousJob(j), next = nextJob(j);
        Position pos = getCoordinates(j.getLocation());

        //ako nema prethodnog posla prethodna lokacija je kucna lokacija korisnika
        Position prevPosition = getCoordinates(j.getUserid().getLocation());
        if (prev != null) {
            prevPosition = getCoordinates(prev.getLocation());
        }

        LocalDateTime prevEndTime = LocalDateTime.now();
        if (prev != null) {
            prevEndTime = LocalDateTime.ofInstant(prev.getTime().toInstant(), ZoneId.systemDefault()).plusMinutes(prev.getDuration());
        }
        
        LocalDateTime currentStartTime = LocalDateTime.ofInstant(j.getTime().toInstant(), ZoneId.systemDefault());
        int travelDuration = travelDuration(pos, prevPosition);
        if (prevEndTime.plusSeconds(travelDuration).isAfter(currentStartTime)) {
            System.out.println("Nema vremena da se stigne sa prethodne obaveze. Obustavlja se dodavanje.");
            return false;
        }

        if (next != null) {
            LocalDateTime nextStartTime = LocalDateTime.ofInstant(prev.getTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime currentEndTime = LocalDateTime.ofInstant(j.getTime().toInstant(), ZoneId.systemDefault()).plusMinutes(j.getDuration());;
            travelDuration = travelDuration(pos, getCoordinates(next.getLocation()));
            if (currentEndTime.plusSeconds(travelDuration).isAfter(nextStartTime)) {
                System.out.println("Nema vremena da se stigne na sledecu. Obustavlja se dodavanje.");
                return false;
            }
        }

        return true;
    }

    private static Position getCoordinates(String location) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://geocode.search.hereapi.com/v1/geocode?q="
                            + location
                            + "&apiKey=" + apiKey)
                    .build();
            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();

            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("items");

            Double lat = items.getJSONObject(0).getJSONObject("position").getDouble("lat");
            Double lng = items.getJSONObject(0).getJSONObject("position").getDouble("lng");

            return new Position(lat, lng);

        } catch (IOException ex) {
            Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static int travelDuration(Position p1, Position p2) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://router.hereapi.com/v8/routes?transportMode=car&origin="
                            + p1.getLat() + "," + p1.getLng()
                            + "&destination=" + p2.getLat() + "," + p2.getLng()
                            + "&return=summary"
                            + "&apiKey=" + apiKey)
                    .build();
            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();

            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("routes");
            int duration = items.getJSONObject(0).getJSONArray("sections").getJSONObject(0).getJSONObject("summary").getInt("duration");

            return duration;

        } catch (IOException ex) {
            Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    private void saveJob(JobRequest jr) {
        try {
            Job j = new Job();
            j.setDescription(jr.getDescription());
            j.setLocation(jr.getLocation());
            j.setTime(jr.getTime());
            j.setUserid(em.createNamedQuery("User.findByIdUser", User.class).setParameter("idUser", jr.getUserid()).getSingleResult());
            j.setDuration(jr.getDuration());

            if (canFitJob(j)) {
                em.getTransaction().begin();
                em.persist(j);
                em.getTransaction().commit();
                if (jr.getAlarmbool()) {
                    setAlarmForJob(j);
                }
                System.out.println("Uspesno dodata obaveza u planer");
            }
        } catch (Exception ex) {
            Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateJob(int idJob, JobRequest jr) {
        Job j = em.createNamedQuery("Job.findByIdJob", Job.class).setParameter("idJob", idJob).getSingleResult();
        if (j == null) {
            return;
        } else {
            j.setDescription(jr.getDescription());
            j.setLocation(jr.getLocation());
            j.setTime(jr.getTime());
            j.setDuration(jr.getDuration());

            // obrisi stari alarm
            if (j.getAlarmid() != null) {
                em.getTransaction().begin();
                em.remove(j.getAlarmid());
                em.getTransaction().commit();
            }
            j.setAlarmid(null);

            // dodaj novi alarm
            if (jr.getAlarmbool()) {
                setAlarmForJob(j);
            }

            if (canFitJob(j)) {
                em.getTransaction().begin();
                em.merge(j);
                em.getTransaction().commit();
            }

        }
    }

    private static void setAlarmForJob(Job j) {
        Alarm a = new Alarm();

        Position p = getCoordinates(j.getUserid().getLocation());
        Job prev = previousJob(j);
        if (prev != null) {
            p = getCoordinates(prev.getLocation());
        }
        int travelDuration = travelDuration(p, getCoordinates(j.getLocation()));
        LocalDateTime alarmTime = LocalDateTime.ofInstant(j.getTime().toInstant(), ZoneId.systemDefault()).minusSeconds(travelDuration);

        a.setStatus(new Integer(1).shortValue());
        a.setIdSong(0);
        a.setRepeating(new Integer(0).shortValue());
        a.setRepeatCount(0);
        a.setRepeatCountTotal(0);
        a.setRepeatPeriod(0);
        a.setTime(java.sql.Timestamp.valueOf(alarmTime));
        j.setAlarmid(a);
        em.getTransaction().begin();
        em.persist(a);
        em.merge(j);
        em.getTransaction().commit();
    }

    private void deleteJob(int idJob) {
        Job j = em.createNamedQuery("Job.findByIdJob", Job.class).setParameter("idJob", idJob).getSingleResult();
        if (j == null) {
            System.out.println("Posao sa tim idem ne postoji");
            return;
        } else {
            em.getTransaction().begin();
            em.remove(j);
            em.getTransaction().commit();
        }
    }

    private void listJobs(int idUser) {
        JMSContext context = connFactory.createContext();
        JMSProducer producer = context.createProducer();

        List<User> users = em.createNamedQuery("User.findByIdUser", User.class).setParameter("idUser", idUser).getResultList();
        if (users.size() == 0) {
            System.out.println("User was not found.");
            return;
        }
        User user = users.get(0);

        ArrayList<String> jobs = new ArrayList<>();
        List<Job> usersJobs = em.createQuery("SELECT j FROM Job j WHERE j.userid=:id",Job.class).setParameter("id", user).getResultList();
        for (Job j : usersJobs) {
            jobs.add(j.toString());
        }

        if (jobs.size() == 0) {
            System.out.println("This user has no jobs in planner");
            return;
        }

        ObjectMessage objectMessage = context.createObjectMessage(jobs);
        /*try {
            objectMessage.setIntProperty("idUser", idUser);
        } catch (JMSException ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("sending object message");*/
        producer.send(jobListQueue, objectMessage);

    }

    public Planner() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        em = emf.createEntityManager();
    }

    public static void main(String[] args) {
        Planner p = new Planner();

        JMSContext context = connFactory.createContext();
        JMSConsumer consumer = context.createConsumer(jobQueue);

        while (true) {
            try {
                Message msg = consumer.receive();
                if (!(msg instanceof ObjectMessage)) {

                    //textmessage is for List and Delete commands
                    if (msg instanceof TextMessage) {
                        String text = ((TextMessage) msg).getText();
                        if (text.split("#")[0].equals("list")) {
                            p.listJobs(Integer.parseInt(text.split("#")[1]));
                        } else if (text.split("#")[0].equals("del")) {
                            p.deleteJob(Integer.parseInt(text.split("#")[1]));
                        }
                    }
                } else {
                    //objectmessage is for insert and update commands
                    ObjectMessage om = (ObjectMessage) msg;
                    if (!(om.getObject() instanceof JobRequest)) {
                        System.out.println("Bad request");
                        continue;
                    }
                    JobRequest jr = (JobRequest) om.getObject();
                    //idJob =  0 ----> INSERT
                    //idJob != 0 ----> UPDATE(idJob)
                    if (om.getIntProperty("idJob") != 0) {
                        p.updateJob(om.getIntProperty("idJob"), jr);
                    } else {
                        p.saveJob(jr);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                break;
            }
        }
    }

}
