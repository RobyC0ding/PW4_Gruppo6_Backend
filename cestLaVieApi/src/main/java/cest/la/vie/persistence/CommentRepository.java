package cest.la.vie.persistence;

import cest.la.vie.persistence.model.Comment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class CommentRepository implements PanacheMongoRepository<Comment> {

    // Metodo per creare un nuovo commento
    public void createComment(Integer userId, ObjectId orderId, String message) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setOrderId(orderId);
        comment.setMessage(message);
        comment.setCreationDate(LocalDateTime.now());
        persist(comment);
    }

    // Metodo per modificare un commento esistente
    public boolean updateComment(ObjectId commentId, String newMessage) {
        Optional<Comment> commentOptional = findByIdOptional(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setMessage(newMessage);
            update(comment);
            return true;
        } else {
            return false;
        }
    }
}
