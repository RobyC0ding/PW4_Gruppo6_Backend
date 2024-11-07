package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public List<Product> findAllProducts() {
        return listAll();
    }

    public List<Product> findByCategory(String categoryName) {
        return list("category.name LIKE ?1", "%" + categoryName + "%");
    }

    public List<Product> findByName(String name) {
        return list("name LIKE ?1", "%" + name + "%");
    }

    public void addProduct(Product product) {
        persist(product);
    }

    public void deleteProduct(Product product) {
        delete(product);
    }

    public void updateProduct(Product product) {
        getEntityManager().merge(product);
    }
}