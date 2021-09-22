/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import JobRequest.JobRequest;
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
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Kosta
 */
@Path("login")
@Stateless
public class LoginResource {
    @PersistenceContext
    EntityManager em;

    @GET
    public Response loginM(@Context HttpHeaders httpHeaders, @QueryParam("username")String username,
            @QueryParam("password")String password) {

        User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

        if (user == null) {
            System.out.println("User not found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        if(!user.getPassword().equals(password)){
            System.out.println("Incorrect password");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        return Response.status(Response.Status.OK).entity("1").build();
    }
}
