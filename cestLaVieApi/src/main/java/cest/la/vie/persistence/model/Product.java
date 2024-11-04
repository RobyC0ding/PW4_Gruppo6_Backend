package cest.la.vie.persistence.model;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Product extends PanacheEntity {
    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "quantity")
    private int quantity;
}
