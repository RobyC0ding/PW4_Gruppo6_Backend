    package cest.la.vie.persistence.model;

    import io.quarkus.mongodb.panache.PanacheMongoEntity;

    import java.time.LocalDateTime;
    import java.util.List;

    public class Order extends PanacheMongoEntity {

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public LocalDateTime getPickupDate() {
            return pickupDate;
        }

        public void setPickupDate(LocalDateTime pickupDate) {
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

        public List<ProductMongo> getProducts() {
            return products;
        }

        public void setProducts(List<ProductMongo> products) {
            this.products = products;
        }

        private Integer userId;
        private LocalDateTime pickupDate;
        private String pickupTime;
        private String status;
        private List<Comment> comments;
        private List<ProductMongo> products;

        public Order() {
            // Costruttore vuoto
        }

        public Order(Integer userId, LocalDateTime pickupDate, String pickupTime, String status, List<Comment> comments, List<ProductMongo> products) {
            this.userId = userId;
            this.pickupDate = pickupDate;
            this.pickupTime = pickupTime;
            this.status = status;
            this.comments = comments;
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
