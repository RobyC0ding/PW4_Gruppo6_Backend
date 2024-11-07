package cest.la.vie.rest;

import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.UserRepository;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import cest.la.vie.service.EmailService;
import cest.la.vie.service.UserService;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import cest.la.vie.service.HashCalculator;


@Path("/auth")
public class AuthenticationResource {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;
    private final HashCalculator hashCalculator;
    private final UserService userService;

    public AuthenticationResource(UserRepository userRepository, SessionRepository sessionRepository, EmailService emailService, HashCalculator hashCalculator, UserService userService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
        this.hashCalculator=hashCalculator;
        this.userService=userService;
    }



    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(User user) {
        // Controllo se l'email è già registrata
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Response.status(Response.Status.CONFLICT).entity("Email già registrata").build();
        }
        // Controllo se il numero di telefono è già registrato
        if (userRepository.findByPhone(user.getPhoneNumber()).isPresent()) {
            return Response.status(Response.Status.CONFLICT).entity("Telefono già registrata").build();
        }

        //Modifico la password dell'utente in hash
        user.setPassword(hashCalculator.calculateHash(user.getPassword()));

        // Persisti l'utente nel database
        userRepository.persist(user);

        // Crea una sessione di verifica e salvala
        Session verificationSession = new Session(user);
        sessionRepository.persist(verificationSession);

        // Invia l'email di verifica con la sessionKey
        emailService.sendVerificationEmail(user, verificationSession.getSessionKey());

        return Response.status(Response.Status.CREATED).entity("Registrazione effettuata, verifica l'email").build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(JsonObject loginRequest){
        String email = loginRequest.getString("email", null); // Restituisce null se la chiave non esiste
        String phoneNumber = loginRequest.getString("phoneNumber", null); // Restituisce null se la chiave non esiste
        String password = loginRequest.getString("password", null); // Aggiungi un campo per la password

        // Verifica se è presente o l'email o il numero di telefono
        if ((email == null || email.isEmpty()) && (phoneNumber == null || phoneNumber.isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email o numero di telefono devono essere forniti").build();
        }

        User user=null;
        // Trova l'utente dall'email o dal il numero di telefono
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user = userRepository.findByPhone(phoneNumber).get();
        } else if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email).get();
        }else{
            return Response.status(Response.Status.BAD_REQUEST).entity("Credenziali sbagliate").build();
        }

        // Verifica se l'utente è verificato
        if(!user.isVerified()){
            return Response.status(Response.Status.BAD_REQUEST).entity("Utente non verificato").build();
        }

        //Verifica se la password dell'utente è uguale alla password inserita
        if((user.getPassword()).equals(hashCalculator.calculateHash(password))){
            // Crea una sessione di verifica e salvala
            Session accessSession = new Session(user);
            sessionRepository.persist(accessSession);
            // Crea cookie di sessione per la risposta frontend
            NewCookie sessionCookie = new NewCookie("SESSION_ID", String.valueOf(accessSession.getSessionKey()), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false);
            return Response.status(Response.Status.ACCEPTED).cookie(sessionCookie).entity("Login effettuata, ciao "+user.getName()).build();
        }else{
            return Response.status(Response.Status.BAD_REQUEST).entity("Credenziali sbagliate").build();
        }

    }
}
