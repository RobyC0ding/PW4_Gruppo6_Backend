package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {
    // Puoi aggiungere metodi personalizzati se necessario

    // Metodo per trovare una categoria per ID
    public Category findById(Long id) {
        return find("id", id).firstResult();
    }
}
