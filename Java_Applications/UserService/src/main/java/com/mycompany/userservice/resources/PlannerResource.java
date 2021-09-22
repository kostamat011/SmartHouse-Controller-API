/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import JobRequest.JobRequest;
import entities.Alarm;
import entities.Job;
import entities.Song;
import entities.User;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Kosta
 */
@Path("planner")
@Stateless
public class PlannerResource {

    @PersistenceContext
    EntityManager em;

    @GET
    public Response insertJob(@Context HttpHeaders httpHeaders, @QueryParam("description") String description,
            @QueryParam("startTime") String startTime, @QueryParam("duration") int duration,
            @QueryParam("location") String locaiton, @QueryParam("alarmbool") int alarmbool) {

        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");

        if (authHeaderValues == null || authHeaderValues.size() <= 0) {
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password

        User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

        if (user == null) {
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int idUser = user.getIdUser();

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("jobConnectionFactory");
            Queue queue = (Queue) context.lookup("myJobQueue");

            JobRequest j = new JobRequest();
            j.setDescription(description);
            j.setUserid(user.getIdUser());
            j.setLocation(locaiton);
            j.setDuration(duration);
            if (alarmbool == 0) {
                j.setAlarmbool(false);
            } else {
                j.setAlarmbool(true);
            }

            try {
                Date d = new SimpleDateFormat("MM dd yyyy HH:mm:ss").parse(startTime);
                j.setTime(d);
            } catch (ParseException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid datetime string").build();
            }

            JMSContext jmscontext = connFactory.createContext();
            ObjectMessage objMsg = jmscontext.createObjectMessage(j);
            objMsg.setIntProperty("idJob", 0);
            JMSProducer producer = jmscontext.createProducer();

            producer.send(queue, objMsg);

            return Response.ok().entity("Adding new job").build();
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("update")
    public Response updateJob(@Context HttpHeaders httpHeaders, @QueryParam("description") String description,
            @QueryParam("startTime") String startTime, @QueryParam("duration") int duration,
            @QueryParam("location") String locaiton, @QueryParam("alarmbool") int alarmbool,
            @QueryParam("idJob") int idJob) {

        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");

        if (authHeaderValues == null || authHeaderValues.size() <= 0) {
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password

        User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

        if (user == null) {
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int idUser = user.getIdUser();

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("jobConnectionFactory");
            Queue queue = (Queue) context.lookup("myJobQueue");

            JobRequest j = new JobRequest();
            j.setDescription(description);
            j.setUserid(user.getIdUser());
            j.setLocation(locaiton);
            j.setDuration(duration);
            if (alarmbool == 0) {
                j.setAlarmbool(false);
            } else {
                j.setAlarmbool(true);
            }

            try {
                Date d = new SimpleDateFormat("MM dd yyyy HH:mm:ss").parse(startTime);
                j.setTime(d);
            } catch (ParseException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid datetime string").build();
            }

            JMSContext jmscontext = connFactory.createContext();
            ObjectMessage objMsg = jmscontext.createObjectMessage(j);
            objMsg.setIntProperty("idJob", idJob);

            JMSProducer producer = jmscontext.createProducer();

            producer.send(queue, objMsg);

            return Response.ok().entity("Updating job " + idJob).build();
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        }
    }

    // request for list of jobs for user 
    @GET
    @Path("remove")
    public Response deleteJob(@Context HttpHeaders httpHeaders, @QueryParam("idJob") int idJob) {

        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");

        if (authHeaderValues == null || authHeaderValues.size() <= 0) {
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password

        User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

        if (user == null) {
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Job j = em.createNamedQuery("Job.findByIdJob", Job.class).setParameter("idJob", idJob).getSingleResult();
        if (j == null || (j.getUserid().getIdUser() != user.getIdUser())) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Zadati posao ne postoji medju vasim poslovima").build();
        }

        int idUser = user.getIdUser();

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("jobConnectionFactory");
            Queue queue = (Queue) context.lookup("myJobQueue");

            JMSContext jmscontext = connFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();

            producer.send(queue, "del#" + idJob);

            return Response.ok().entity("Deleting job number " + idJob).build();
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    // request for list of jobs for user 
    @GET
    @Path("list")
    public Response listJobs(@Context HttpHeaders httpHeaders) {

        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");

        if (authHeaderValues == null || authHeaderValues.size() <= 0) {
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password

        User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

        if (user == null) {
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int idUser = user.getIdUser();

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("jobConnectionFactory");
            Queue queue = (Queue) context.lookup("myJobQueue");

            JMSContext jmscontext = connFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();

            producer.send(queue, "list#" + user.getIdUser());

            return Response.ok().entity("Adding new job").build();
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    // response for list of jobs for user 
    @GET
    @Path("list/response")
    public Response receiveJobsList() {
        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("jobConnectionFactory");
            Queue queue = (Queue) context.lookup("myJobListQueue");

            JMSConsumer consumer = connFactory.createContext().createConsumer(queue);
            Message msg = consumer.receive();

            if (msg instanceof ObjectMessage) {
                ObjectMessage objmsg = (ObjectMessage) msg;
                ArrayList<String> joblist = (ArrayList<String>) objmsg.getObject();
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("line.separator"));
                for (String s : joblist) {
                    sb.append(s);
                    sb.append(System.getProperty("line.separator"));
                }
                //return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(joblist) {
                //}).build();
                return Response.status(Response.Status.OK).entity(sb.toString()).build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }
}
