package cest.la.vie.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class Session extends PanacheEntity {
    @Column(name = "session_key", length = 70)
    private String sessionKey;

    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
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
