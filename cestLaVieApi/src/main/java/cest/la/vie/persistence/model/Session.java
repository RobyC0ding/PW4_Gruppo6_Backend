package cest.la.vie.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Session extends PanacheEntity {
    @Column(name = "session_key", length = 70)
    private String sessionKey;

    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;
}
