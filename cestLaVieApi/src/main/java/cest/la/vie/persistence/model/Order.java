package cest.la.vie.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;


@MongoEntity(collection = "orders")
public class Order extends PanacheMongoEntityBase {

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @BsonId
    private ObjectId id;


    @BsonProperty("user_id")
    @JsonProperty("user_id")
    private Integer userId;
    @BsonProperty("pickup_date")
    @JsonProperty("pickup_date")
    private Instant pickupDate;
    @BsonProperty("pickup_time")
    @JsonProperty("pickup_time")
    private String pickupTime;

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @BsonProperty("creation_date")
    @JsonProperty("creation_date")
    private Instant creationDate;
    @BsonProperty("status")
    private String status;
    @BsonProperty("comments")
    private List<Comment> comments;
    @BsonProperty("products")
    private List<Product> products;

    public Order() {
        // Costruttore vuoto
    }

    public Order(ObjectId id, Instant pickupDate, String pickupTime, String status, List<Comment> comments, List<Product> products) {
        this.id = id;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.status = status;
        this.comments = comments;
        this.products = products;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Instant getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Instant pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public enum Status {
        PENDING("pending"),
        ACCEPTED("accepted"),
        REFUSED("refused"),
        TAKEN("taken");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "sqluserId=" + userId +
                ", pickupDate=" + pickupDate +
                ", pickupTime='" + pickupTime + '\'' +
                ", creationDate=" + creationDate +
                ", status='" + status + '\'' +
                ", comments=" + comments +
                ", products=" + products +
                '}';
    }
}
