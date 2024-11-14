package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Order;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.bson.types.ObjectId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheMongoRepository<Order> {

    // Aggiungi un ordine
    public void addOrder(Order order) {
        persist(order);
    }

    // Modifica lo stato di un ordine
    public boolean updateOrderStatus(ObjectId orderId, String status) {
        Order order = findById(orderId);
        if (order != null) {
            order.setStatus(status);
            update(order);
            return true;
        }
        return false;
    }

    // Trova tutti gli ordini di un utente
    public List<Order> findOrdersByUser(ObjectId userId) {
        return list("userId", userId);
    }

    // Trova tutti gli ordini con uno stato specifico
    public List<Order> findOrdersByStatus(String status) {
        return list("status", status);
    }

    // Trova tutti gli ordini
    public List<Order> getAll() {
        return listAll();
    }
}
