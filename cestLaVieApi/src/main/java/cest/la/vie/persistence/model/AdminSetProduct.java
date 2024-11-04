package cest.la.vie.persistence.model;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AdminSetProduct extends PanacheEntity {
    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "Product_Id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;
}