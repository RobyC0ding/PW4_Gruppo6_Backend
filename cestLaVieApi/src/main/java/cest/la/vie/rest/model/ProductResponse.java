package cest.la.vie.rest.model;

import cest.la.vie.persistence.model.Ingredient;
import cest.la.vie.persistence.model.Product;

import java.util.List;

public class ProductResponse {

    private Product product;
    private List<Ingredient> ingredients;

    public ProductResponse(Product product, List<Ingredient> ingredients) {
        this.product = product;
        this.ingredients = ingredients;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
