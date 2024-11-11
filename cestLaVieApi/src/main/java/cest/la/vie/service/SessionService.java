package cest.la.vie.service;

import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionService {

    public final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }


    public Session createSession(User user){
        // Crea una sessione di verifica e salvala
        Session verificationSession = new Session(user);
        sessionRepository.persist(verificationSession);
        return verificationSession;
    }

}
