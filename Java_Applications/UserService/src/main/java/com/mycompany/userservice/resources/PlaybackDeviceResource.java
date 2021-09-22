/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import entities.User;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
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
@Path("playbackdevice")
@Stateless
public class PlaybackDeviceResource {
    @PersistenceContext
    EntityManager em;
    
    @GET
    public Response ping(@Context HttpHeaders httpHeaders, @QueryParam("songName")String songName){
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
            
            if(songName==null){
                System.out.println("Empty song name");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            
            JMSProducer producer = connFactory.createContext().createProducer();
            producer.send(queue, songName+"#"+idUser);
            
            return Response.ok().entity("User: "+idUser+" requested to play song: "+songName).build();
        }catch(NamingException ex){
            System.out.println("JMS Resource naming error");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        



        
    }
    
}
