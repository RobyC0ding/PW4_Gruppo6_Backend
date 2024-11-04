package cest.la.vie.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class User extends PanacheEntity {
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "surname", length = 255)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('A', 'C')")
    private Role role;

    public enum Role {
        A, // Admin
        C  // Customer
    }
}
