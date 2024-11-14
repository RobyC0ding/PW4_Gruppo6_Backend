package cest.la.vie.persistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@MongoEntity(collection = "comments")
public class Comment extends PanacheMongoEntity {

    @BsonProperty("_id")
    private ObjectId id;

    @BsonProperty("user_id")
    private Integer userId;

    @BsonProperty("order_id")
    private ObjectId orderId;

    @BsonProperty("message")
    private String message;

    @BsonProperty("creation_date")
    private LocalDateTime creationDate;

    public Comment() {
        // Costruttore vuoto
    }

    public Comment(ObjectId id, Integer userId, ObjectId orderId, String message, LocalDateTime creationDate) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.message = message;
        this.creationDate = creationDate;
    }

    // Getters e Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ObjectId getOrderId() {
        return orderId;
    }

    public void setOrderId(ObjectId orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", message='" + message + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
