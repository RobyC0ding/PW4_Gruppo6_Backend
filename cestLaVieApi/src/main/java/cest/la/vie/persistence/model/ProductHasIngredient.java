package cest.la.vie.persistence.model;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.io.Serializable;

@Entity
@Table(name = "Product_has_ingredient")
@IdClass(ProductHasIngredientId.class)
public class ProductHasIngredient extends PanacheEntityBase implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "Product_id", nullable = false)
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "Ingredient_Id", nullable = false)
    private Ingredient ingredient;

    // Getters and setters

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
