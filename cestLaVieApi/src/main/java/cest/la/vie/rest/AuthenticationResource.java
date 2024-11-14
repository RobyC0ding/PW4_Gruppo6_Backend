package cest.la.vie.rest;

import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.UserRepository;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import cest.la.vie.service.*;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


@Path("/auth")
public class AuthenticationResource {
    private final UserRepository userRepository;

    private final SessionService sessionService;
    private final EmailService emailService;
    private final HashCalculator hashCalculator;
    private final UserService userService;

    private final AuthenticationService authenticationService;

    public AuthenticationResource(UserRepository userRepository,  SessionService sessionService, EmailService emailService, HashCalculator hashCalculator, UserService userService, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.emailService = emailService;
        this.hashCalculator=hashCalculator;
        this.userService=userService;
        this.authenticationService = authenticationService;
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

        // Crea una sessione di verifica
        Session verificationSession =sessionService.createSession(user);

        // Invia l'email di verifica con la sessionKey o non invia niente se registrato con numero di telefono sarà l'admin ad accettare gli utenti
        if(!user.getEmail().isEmpty() && user.getEmail()!=null){
            emailService.sendVerificationEmail(user, verificationSession.getSessionKey());
        }

        return Response.status(Response.Status.CREATED).entity("Registrazione effettuata, verifica l'email").build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response login(JsonObject loginRequest){
        String email = loginRequest.getString("email", null); // Restituisce null se la chiave non esiste
        String phoneNumber = loginRequest.getString("phoneNumber", null); // Restituisce null se la chiave non esiste
        String password = loginRequest.getString("password", null); // Aggiungi un campo per la password

        // Verifica se è presente o l'email o il numero di telefono
        if ((email == null || email.isEmpty()) && (phoneNumber == null || phoneNumber.isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email o numero di telefono devono essere forniti").build();
        }

        // Prende l'utente dal db
        User user=authenticationService.takeUserfromLoginRequest(email,phoneNumber);

        // Verifica se l'utente è verificato
        if(!user.isVerified()){
            return Response.status(Response.Status.BAD_REQUEST).entity("Utente non verificato").build();
        }

        // Verifica se la password dell'utente è uguale alla password inserita
        if(authenticationService.verifyPassword(user,password)){
            // Crea una sessione di accesso e salvala
            Session accessSession =sessionService.createSession(user);
            // Crea cookie di sessione per la risposta frontend
            NewCookie sessionCookie = new NewCookie("SESSION_ID", String.valueOf(accessSession.getSessionKey()), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false);
            return Response.status(Response.Status.ACCEPTED).cookie(sessionCookie).entity("Login effettuata, ciao "+user.getName()).build();
        }else{
            return Response.status(Response.Status.BAD_REQUEST).entity("Credenziali sbagliate").build();
        }

    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response logout(@CookieParam("SESSION_ID") String sessionId) {
        // Controlla se il cookie di sessione esiste
        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nessuna sessione trovata. Effettua prima il login.").build();
        }

        // Cerca la sessione nel repository
        boolean sessionRemoved = sessionService.invalidateSession(sessionId);
        if (sessionRemoved) {
            // Rimuove il cookie dal client impostandolo con max-age 0
            NewCookie expiredCookie = new NewCookie("SESSION_ID", null, "/", null, null, 0, false);
            return Response.ok().cookie(expiredCookie).entity("Logout effettuato con successo").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Sessione non trovata o già terminata").build();
        }
    }


}
