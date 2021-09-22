/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import AlarmRequest.AlarmRequest;
import entities.Alarm;
import entities.Song;
import entities.User;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

/**
 *
 * @author Kosta
 */
@Path("alarm2")
@Stateless
public class AlarmManagerResource {

    @PersistenceContext
    EntityManager em;

    boolean songFound = false;

    @GET
    public Response ping(@Context HttpHeaders httpHeaders, @QueryParam("timeString") String timeString,
            @QueryParam("repeating") short repeating, @QueryParam("repeatPeriod") int repeatPeriod,
            @QueryParam("repeatCountTotal") int repeatCountTotal, @QueryParam("song") String songName) {

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
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("alarmConnectionFactory");
            Queue queue = (Queue) context.lookup("myAlarmQueue");

            AlarmRequest a = new AlarmRequest();
            a.setStatus(new Integer(1).shortValue());
            if (songName.equals("")) {
                a.setIdSong(0);
            } else {
                Song song = em.createNamedQuery("Song.findByName", Song.class).setParameter("name", songName).getSingleResult();
                if (song == null) {
                    a.setIdSong(0);
                } else {
                    a.setIdSong(song.getIdSong());
                    songFound = true;
                }
            }

            if (repeating == 0) {
                a.setRepeating(repeating);
                a.setRepeatCount(0);
                a.setRepeatCountTotal(0);
                a.setRepeatPeriod(0);
            } else {
                a.setRepeating(new Integer(1).shortValue());
                a.setRepeatCount(0);
                a.setRepeatCountTotal(repeatCountTotal);
                a.setRepeatPeriod(repeatPeriod);
            }

            try {
                Date d = new SimpleDateFormat("MM dd yyyy HH:mm:ss").parse(timeString);
                a.setTime(d);
            } catch (ParseException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid datetime string").build();

            }

            JMSContext jmscontext = connFactory.createContext();
            ObjectMessage objMsg = jmscontext.createObjectMessage(a);
            JMSProducer producer = jmscontext.createProducer();

            producer.send(queue, objMsg);
            
            if(!songFound){
                return Response.ok().entity("song not found").build();
            }
            return Response.ok().entity("New alarm added at " + timeString).build();
        } catch (NamingException ex) {
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

    }
}
