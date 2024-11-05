package cest.la.vie.rest;

import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.UserRepository;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import cest.la.vie.service.EmailService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    @Inject
    EmailService emailService;

    @POST
    @Path("/register")
    public Response register(User user) {
        // Controllo se l'email è già registrata
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Response.status(Response.Status.CONFLICT).entity("Email già registrata").build();
        }

        // Persisti l'utente nel database
        userRepository.persist(user);

        // Crea una sessione di verifica e salvala
        Session verificationSession = new Session(user);
        sessionRepository.persist(verificationSession);

        // Invia l'email di verifica con la sessionKey
        emailService.sendVerificationEmail(user, verificationSession.getSessionKey());

        return Response.status(Response.Status.CREATED).entity("Registrazione effettuata, verifica l'email").build();
    }

    @GET
    @Path("/verify")
    public Response verify(@QueryParam("sessionKey") String sessionKey) {
        Optional<Session> optionalSession = sessionRepository.findBySessionKey(sessionKey);
        if (optionalSession.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chiave di sessione non valida").build();
        }
        User user = optionalSession.get().getUser();
        user.setVerified(true);  // Assicurati di avere un setter o un campo booleano per la verifica
        userRepository.persist(user);
        return Response.ok("Account verificato con successo").build();
    }
}
