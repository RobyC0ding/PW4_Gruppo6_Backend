package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class IngredientRepository implements PanacheRepository<Ingredient> {

    public void addIngredient(Ingredient ingredient) {
        persist(ingredient);
    }

    public void removeIngredient(Long id) {
        Ingredient ingredient = findById(id);
        if (ingredient != null) {
            delete(ingredient);
        }
    }

    public List<Ingredient> getAllIngredients() {
        return listAll();
    }
}
