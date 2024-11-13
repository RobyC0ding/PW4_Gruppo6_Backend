package cest.la.vie.persistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Comment extends PanacheMongoEntity {

    public Integer userId;
    public ObjectId orderId;
    public String message;
    public LocalDateTime creationDate;

    public Comment() {
        // Costruttore vuoto
    }

    public Comment(Integer userId, ObjectId orderId, String message, LocalDateTime creationDate) {
        this.userId = userId;
        this.orderId = orderId;
        this.message = message;
        this.creationDate = creationDate;
    }
}
