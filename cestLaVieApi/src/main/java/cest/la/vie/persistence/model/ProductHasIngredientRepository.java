package cest.la.vie.persistence.model;

import cest.la.vie.persistence.model.ProductHasIngredient;
import cest.la.vie.persistence.model.Product;
import cest.la.vie.persistence.model.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductHasIngredientRepository implements PanacheRepository<ProductHasIngredient> {

    public List<ProductHasIngredient> findIngredientsByProduct(Product product) {
        return list("product", product);
    }

    public void addIngredientToProduct(ProductHasIngredient productHasIngredient) {
        persist(productHasIngredient);
    }

    public void removeIngredientFromProduct(Product product, Ingredient ingredient) {
        delete("product = ?1 and ingredient = ?2", product, ingredient);
    }
    
}
