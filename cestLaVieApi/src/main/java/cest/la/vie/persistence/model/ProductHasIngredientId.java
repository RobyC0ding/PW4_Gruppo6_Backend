package cest.la.vie.persistence.model;

import java.io.Serializable;
import java.util.Objects;

public class ProductHasIngredientId implements Serializable {
    private int product;
    private int ingredient;

    // hashCode e equals per l'ID composto
    @Override
    public int hashCode() {
        return Objects.hash(product, ingredient);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductHasIngredientId that = (ProductHasIngredientId) obj;
        return product == that.product && ingredient == that.ingredient;
    }
}
