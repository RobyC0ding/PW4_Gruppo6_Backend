package cest.la.vie.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Admin_set_product")
@IdClass(AdminSetProductId.class)
public class AdminSetProduct extends PanacheEntityBase implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "Product_Id", nullable = false)
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Getters and setters

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
