package cest.la.vie.service;

import cest.la.vie.persistence.model.Category;
import cest.la.vie.persistence.model.Product;
import cest.la.vie.persistence.CategoryRepository;

import cest.la.vie.rest.model.ProductRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {


    private final CategoryRepository categoryRepository;

    public ProductService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Product convertToProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(Double.parseDouble(productRequest.getPrice().replace("€", "").trim()));
        product.setQuantity(Integer.parseInt(productRequest.getQuantity()));
        product.setDescription(productRequest.getDescription());

        // Verifica della categoria
        Category category = categoryRepository.findById(Long.parseLong(productRequest.getCategory()));
        if (category != null) {
            product.setCategory(category);
        } else {
            throw new IllegalArgumentException("Categoria non trovata.");
        }

        // Altre mappature degli attributi se necessario

        return product;
    }
}
