package cest.la.vie.persistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@MongoEntity(collection = "orders")
public class Order extends PanacheMongoEntity {


    @BsonProperty("user_id")
    private Integer sqluserId;
    @BsonProperty("pickup_date")
    private Instant pickupDate;
    @BsonProperty("pickup_time")
    private String pickupTime;

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @BsonProperty("creation_date")
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

    public ObjectId getUserId() {
        return id;
    }

    public void setUserId(ObjectId id) {
        this.id = id;
    }

    public Integer getSqluserId() {
        return sqluserId;
    }

    public void setSqluserId(Integer sqluserId) {
        this.sqluserId = sqluserId;
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
        MAKING("making"),
        READY("ready");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
