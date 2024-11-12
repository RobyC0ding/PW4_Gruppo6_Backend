package cest.la.vie.persistence;

import cest.la.vie.persistence.model.ProductHasIngredient;
import cest.la.vie.persistence.model.Product;
import cest.la.vie.persistence.model.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductHasIngredientRepository implements PanacheRepository<ProductHasIngredient> {

    public List<Ingredient> findIngredientsByProduct(Product product) {
        return getEntityManager().createQuery(
                        "SELECT phi.ingredient FROM ProductHasIngredient phi WHERE phi.product = :product", Ingredient.class)
                .setParameter("product", product)
                .getResultList();
    }

    public void addIngredientToProduct(ProductHasIngredient productHasIngredient) {
        persist(productHasIngredient);
    }

    public void removeIngredientFromProduct(Product product, Ingredient ingredient) {
        delete("product = ?1 and ingredient = ?2", product, ingredient);
    }

}
