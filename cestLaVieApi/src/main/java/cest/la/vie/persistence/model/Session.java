package cest.la.vie.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Session extends PanacheEntityBase {

    @Id
    @Column(name = "id", length = 36, unique = true, nullable = false)
    private String sessionKey;

    @ManyToOne
    @JoinColumn(name = "User_id", nullable = false)
    private User user;

    // Costruttore
    public Session(User user) {
        this.user = user;
        this.sessionKey = generateSessionKey();
    }

    public Session() {

    }

    // Metodo per generare una session key unica
    private String generateSessionKey() {
        return UUID.randomUUID().toString();
    }

    // Getters e setters
    public String getSessionKey() {
        return sessionKey;
    }

    public User getUser() {
        return user;
    }


}
