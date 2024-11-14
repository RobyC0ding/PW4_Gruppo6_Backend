package cest.la.vie.rest;

import cest.la.vie.persistence.OrderRepository;
import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.model.Order;
import cest.la.vie.persistence.model.User;
import cest.la.vie.persistence.model.Session;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.bson.types.ObjectId;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Path("/order")
public class OrderResource {

    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;

    public OrderResource(OrderRepository orderRepository, SessionRepository sessionRepository) {
        this.orderRepository = orderRepository;
        this.sessionRepository = sessionRepository;
    }

    @POST

    public Response createOrder(@Context HttpHeaders httpHeaders, Order order) {
        try {
            // Prende il cookie di sessione
            Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
            if (sessionCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
            }

            // Prende l'utente dalla sessione
            Optional<Session> session = sessionRepository.findBySessionKey(sessionCookie.getValue());
            if (session.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid session. Please log in.").build();
            }
            User user = session.get().getUser();

            // Verifica se l'utente ha il permesso di fare un ordine
            if (user.getRole() != User.Role.C) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("L'utente non ha il permesso di fare questa azione.").build();
            }

            System.out.println(order.toString());

            // Trova tutti gli ordini tranne quelli con stato "refused" o "taken"
            List<Order> existingOrders = orderRepository.getAll();

            System.out.println(existingOrders);

            for (Order existingOrder : existingOrders) {
                System.out.println("ciao stato:"+existingOrder.getStatus());
                System.out.println(!existingOrder.getStatus().equals(Order.Status.REFUSED.getValue()));
                System.out.println(!existingOrder.getStatus().equals(Order.Status.READY.getValue()));
                System.out.println((existingOrder.getPickupDate().equals(order.getPickupDate()))+ " ex:"+existingOrder.getPickupDate()+ " my:"+order.getPickupDate());
                if (!existingOrder.getStatus().equals(Order.Status.REFUSED.getValue()) &&
                        !existingOrder.getStatus().equals(Order.Status.READY.getValue()) && existingOrder.getPickupDate().equals(order.getPickupDate())) {
                    System.out.println("urca");
                    //Converte la stringa del pickupTime in numero
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime pickupTime = LocalTime.parse(existingOrder.getPickupTime(), formatter);
                    int totalSeconds = pickupTime.toSecondOfDay() ;

                    //Converte la stringa in numero
                    LocalTime myOderPickupTime = LocalTime.parse(order.getPickupTime(), formatter);
                    int myOrderTotalSeconds = myOderPickupTime.toSecondOfDay();

                    // Verifica che l'orario non si sovrapponga con gli altri ordini (10 minuti di margine)
                    System.out.println("mio tempo:"+myOrderTotalSeconds+" altro tempo:"+totalSeconds);
                    if (Math.abs(totalSeconds - myOrderTotalSeconds) <= 600) {
                        // Se la differenza è minore o uguale a 600 secondi, c'è una sovrapposizione
                        return Response.status(Response.Status.BAD_REQUEST).entity("Order time overlaps with another order. Please choose a different time.").build();
                    }

                }
            }

            orderRepository.addOrder(order);

            return Response.status(Response.Status.CREATED).entity("Order created successfully!").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create order: " + e.getMessage()).build();
        }
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("id") String id) {
        try {
            // Recupera l'ordine usando l'ObjectId
            Order order = orderRepository.findById(new ObjectId(id));
            if (order == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
            }
            return Response.ok(order).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving order: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByUser(@PathParam("userId") String userId) {
        try {
            List<Order> orders = orderRepository.findOrdersByUser(new ObjectId(userId));
            if (orders.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No orders found for this user").build();
            }
            return Response.ok(orders).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving orders: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrderStatus(@PathParam("id") String id, @QueryParam("status") String status) {
        try {
            // Aggiorna lo stato dell'ordine
            boolean updated = orderRepository.updateOrderStatus(new ObjectId(id), status);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
            }
            return Response.status(Response.Status.OK).entity("Order status updated successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating order status: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders(){
        return Response.status(Response.Status.OK).entity(orderRepository.getAll()).build();
    }
}
