package cest.la.vie.rest;

import cest.la.vie.persistence.*;
import cest.la.vie.service.*;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

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

}
