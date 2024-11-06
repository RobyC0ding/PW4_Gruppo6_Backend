package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Session;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SessionRepository implements PanacheRepository<Session> {

    // Metodo per trovare una sessione in base alla chiave di sessione
    public Optional<Session> findBySessionKey(String sessionKey) {
        return find("sessionKey", sessionKey).firstResultOptional();
    }

    // Metodo per trovare tutte le sessioni di un utente specifico
    public List<Session> findByUserId(Long userId) {
        return list("user.id", userId);
    }

    // Metodo per eliminare una sessione in base alla chiave di sessione
    public void deleteBySessionKey(String sessionKey) {
        delete("sessionKey", sessionKey);
    }

}