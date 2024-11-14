package cest.la.vie.rest;

import cest.la.vie.persistence.CommentRepository;
import cest.la.vie.persistence.OrderRepository;
import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.model.Comment;
import cest.la.vie.persistence.model.Order;
import cest.la.vie.persistence.model.Session;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Optional;

@Path("/comment")
public class CommentResource {

    private final CommentRepository commentRepository;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;

    public CommentResource(CommentRepository commentRepository, OrderRepository orderRepository, SessionRepository sessionRepository) {
        this.commentRepository = commentRepository;
        this.orderRepository = orderRepository;
        this.sessionRepository = sessionRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createComment(@Context HttpHeaders httpHeaders, @QueryParam("orderId") String orderIdStr, Comment comment) {
        try {
            // Prende il cookie di sessione
            Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
            if (sessionCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
            }

            // Verifica la sessione
            Optional<Session> sessionOpt = sessionRepository.findBySessionKey(sessionCookie.getValue());
            if (sessionOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid session. Please log in.").build();
            }
            Session session = sessionOpt.get();
            Integer userId = session.getUser().getId();

            // Converte l'ID passato in un ObjectId
            ObjectId orderId;
            try {
                orderId = new ObjectId(orderIdStr);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid order ID format").build();
            }

            // Trova l'ordine
            Order order = orderRepository.findById(orderId);
            if (order == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
            }

            // Verifica se l'ordine appartiene all'utente e se è nello stato "taken"
            if (!order.getUserId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("You are not authorized to comment on this order.").build();
            }
            if (!order.getStatus().equals(Order.Status.TAKEN.getValue())) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Comments can only be made on orders with status 'taken'.").build();
            }

            // Imposta i dettagli del commento
            comment.setUserId(userId);
            comment.setOrderId(orderId);
            comment.setCreationDate(LocalDateTime.now());

            // Salva il commento
            System.out.println(comment);
            commentRepository.persist(comment);

            // Aggiorna l'ordine per aggiungere il commento
            orderRepository.addComment(orderId, comment);

            return Response.status(Response.Status.CREATED).entity("Comment created successfully!").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create comment: " + e.getMessage()).build();
        }
    }
}
