package cest.la.vie.rest;

import cest.la.vie.persistence.*;
import cest.la.vie.rest.model.UserResponse;
import cest.la.vie.rest.model.AdminSetVerifiedRequest;
import cest.la.vie.service.*;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/user")
public class UserResource {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserResource(UserRepository userRepository, SessionRepository sessionRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @GET
    @Path("/verify")
    @Transactional
    public Response verify(@QueryParam("sessionKey") String sessionKey) {
        Optional<Session> optionalSession = sessionRepository.findBySessionKey(sessionKey);
        if (optionalSession.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chiave di sessione non valida").build();
        }

        User user = optionalSession.get().getUser();
        // Cambio il flag per la verifica del utente
        user.setVerified(true);
        userRepository.persist(user);

        // Elimina la sessione di verifica usata
        sessionRepository.delete(optionalSession.get());

        // Crea una nuova sessione di accesso
        Session accessSession = new Session(user);
        sessionRepository.persist(accessSession);

        // Crea un cookie di sessione
        NewCookie sessionCookie = new NewCookie("SESSION_ID", String.valueOf(accessSession.getSessionKey()), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false);

        // Restituisce la chiave di sessione di accesso
        return Response.ok("Account verificato con successo, sessione di accesso creata")
                .cookie(sessionCookie)
                .build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpHeaders httpHeaders){
        // Prende il cookie di sessione
        Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
        if (sessionCookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
        }

        // Prende l'utente dalla sessione
        Optional<Session> session=sessionRepository.findBySessionKey(sessionCookie.getValue());
        User u=session.get().getUser();

        // Verifica se l'utente è un admin
        if(u.getRole()!= User.Role.A){
            return Response.status(Response.Status.UNAUTHORIZED).entity("L'utente non ha il permesso di fare questa azione.").build();
        }

        //Prende solo gli utenti che hanno il numero di telefono e non la email
        List<User> users= userRepository.findUsersWithPhoneAndNotVerified();
        System.out.println(users);

        // Converte la lista di utenti in una lista di UtenteResponse
        List<UserResponse> response = users.stream()
                .map(user -> new UserResponse(user.getEmail(), user.getPhoneNumber(), user.isVerified()))
                .collect(Collectors.toList());

        return Response.status(Response.Status.OK).entity(response).build();


    }

    @GET
    @Path("/role")
    public Response getRole(@Context HttpHeaders httpHeaders){
        // Prende il cookie di sessione
        Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
        if (sessionCookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
        }

        // Prende l'utente dalla sessione
        Optional<Session> session=sessionRepository.findBySessionKey(sessionCookie.getValue());
        User u=session.get().getUser();

        // Verifica se l'utente è un admin
        if(u.getRole()== User.Role.A){
            return Response.status(Response.Status.OK).entity("Admin").build();
        }else{
            return Response.status(Response.Status.OK).entity("Client").build();
        }
    }

    @GET
    @Path("/fromSession")
    public Response getId(@Context HttpHeaders httpHeaders){
        // Prende il cookie di sessione
        Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
        if (sessionCookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
        }

        // Prende l'utente dalla sessione
        Optional<Session> session=sessionRepository.findBySessionKey(sessionCookie.getValue());
        User u=session.get().getUser();

        return Response.status(Response.Status.OK).entity(u.getId()).build();
    }

    @PUT
    @Path("/adminVerify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response adminSetUserVerified(@Context HttpHeaders httpHeaders, AdminSetVerifiedRequest userRequest) {
        System.out.println("CIAOOOO "+userRequest);
        // Prende il cookie di sessione
        Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
        if (sessionCookie == null) {
            System.out.println("CIAOOOO2");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
        }

        // Prende l'utente dalla sessione
        Optional<Session> session = sessionRepository.findBySessionKey(sessionCookie.getValue());
        if (session.isEmpty()) {
            System.out.println("CIAOOOO3");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid session.").build();
        }
        User adminUser = session.get().getUser();

        // Verifica se l'utente è un admin
        if (adminUser.getRole() != User.Role.A) {
            System.out.println("CIAOOOO4");
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied. Only admins can verify users.").build();
        }

        // Trova l'utente da verificare
        System.out.println(userRequest.getPhone_number());
        Optional<User> userToVerify = userRepository.findByPhone(userRequest.getPhone_number());
        if (userToVerify.isEmpty()) {
            System.out.println("CIAOOOO5");
            return Response.status(Response.Status.NOT_FOUND).entity("User not found.").build();
        }
        User user = userToVerify.get();

        // Imposta l'utente come verificato
        user.setVerified(true);
        userRepository.persist(user);

        return Response.status(Response.Status.OK).entity("User successfully verified.").build();
    }


}
