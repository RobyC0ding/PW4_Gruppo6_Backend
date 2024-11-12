package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    @Id
    private Long id;
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    public Optional<User> findByPhone(String phone) {
        return find("phoneNumber", phone).firstResultOptional();
    }

    // Nuovo metodo per ottenere gli utenti con il numero di telefono non nullo
    public List<User> findUsersWithPhone() {
        return list("phoneNumber IS NOT NULL");
    }

}