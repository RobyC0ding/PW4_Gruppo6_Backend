package cest.la.vie.persistence.model;

import java.io.Serializable;
import java.util.Objects;

public class AdminSetProductId implements Serializable {
    private int product;
    private int user;

    // Costruttore di default
    public AdminSetProductId() {}

    // Costruttore con parametri
    public AdminSetProductId(int product, int user) {
        this.product = product;
        this.user = user;
    }

    // hashCode e equals per l'ID composto
    @Override
    public int hashCode() {
        return Objects.hash(product, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AdminSetProductId that = (AdminSetProductId) obj;
        return product == that.product && user == that.user;
    }

    // Getters e setters opzionali se necessari
}
