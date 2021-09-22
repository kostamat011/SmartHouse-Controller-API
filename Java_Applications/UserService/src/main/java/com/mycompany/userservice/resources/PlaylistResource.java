/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import entities.Song;
import entities.User;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
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
import playlist.Playlist;

/**
 *
 * @author Kosta
 */
@Path("playlist")
@Stateless
public class PlaylistResource {
    @PersistenceContext
    EntityManager em;
    
    @GET
    public Response ping(@Context HttpHeaders httpHeaders){
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        
        if(authHeaderValues == null || authHeaderValues.size()<=0){
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password
        
        User user = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getSingleResult();
        
        if(user==null){
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        int idUser = user.getIdUser();
        
        try{
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("playbackConnectionFactory");
            Queue queue = (Queue) context.lookup("songQueue");
            
            JMSProducer producer = connFactory.createContext().createProducer();
            producer.send(queue, "playlist#"+idUser);
            
            return Response.ok().entity("User: "+idUser+" requested his playlist. ").build();
        }catch(NamingException ex){
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        
    }
    

    @GET
    @Path("response")
    public Response receivePlaylist(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        
        if(authHeaderValues == null || authHeaderValues.size()<=0){
            System.out.println("Authorization failed");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        // TODO: check password
        
        User user = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getSingleResult();
        
        if(user==null){
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        int idUser = user.getIdUser();
        
         try{
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup("playbackConnectionFactory");
            Topic topic = (Topic) context.lookup("myPlaylistTopic");
            
            JMSConsumer consumer = connFactory.createContext().createSharedDurableConsumer(topic,"sub"+idUser,"idUser="+idUser);
            Message msg = consumer.receive();
           
            if(msg instanceof ObjectMessage){
                ObjectMessage objmsg = (ObjectMessage) msg;
                ArrayList<String> playlist = (ArrayList<String>)objmsg.getObject();
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("line.separator"));
                for (String s : playlist) {
                    sb.append(s);
                    sb.append(System.getProperty("line.separator"));
                }
                //return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(playlist){}).build();
                return Response.status(Response.Status.OK).entity(sb.toString()).build();
            }
            else{
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        }catch(NamingException ex){
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }catch(Exception ex){
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
       }
    }
    
}
    

