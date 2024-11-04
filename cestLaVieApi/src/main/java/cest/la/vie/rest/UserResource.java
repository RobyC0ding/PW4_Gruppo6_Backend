package cest.la.vie.rest;

import cest.la.vie.persistence.UserRepository;
import cest.la.vie.persistence.model.User;
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
    EmailService emailService;

    @POST
    @Path("/register")
    public Response register(User user) {
        if (userRepository.findByEmail(user.email).isPresent()) {
            return Response.status(Response.Status.CONFLICT).entity("Email già registrata").build();
        }
        user.generateVerificationToken();
        userRepository.persist(user);
        emailService.sendVerificationEmail(user);
        return Response.status(Response.Status.CREATED).entity("Registrazione effettuata, verifica l'email").build();
    }

    @GET
    @Path("/verify")
    public Response verify(@QueryParam("token") String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Token non valido").build();
        }
        User user = optionalUser.get();
        user.isVerified = true;
        userRepository.persist(user);
        return Response.ok("Account verificato con successo").build();
    }
}