package cest.la.vie.persistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

public class ProductMongo extends PanacheMongoEntity {

    public ObjectId idProduct;   // Riferimento al prodotto tramite ObjectId
    public String nameProduct;   // Nome del prodotto
    public int quantity;         // Quantità
    public double price;         // Prezzo

    // Costruttore
    public ProductMongo() {
    }

    public ProductMongo(ObjectId idProduct, String nameProduct, int quantity, double price) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters e setters
    public ObjectId getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(ObjectId idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
