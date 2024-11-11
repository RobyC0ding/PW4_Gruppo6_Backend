package cest.la.vie.rest;

import cest.la.vie.persistence.ProductRepository;
import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.model.Product;
import cest.la.vie.persistence.model.Session;
import cest.la.vie.persistence.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Optional;

@Path("/product")
public class ProductResource {

    private final ProductRepository productRepository;
    private final SessionRepository sessionRepository;

    public ProductResource(ProductRepository productRepository, SessionRepository sessionRepository) {
        this.productRepository = productRepository;
        this.sessionRepository = sessionRepository;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("category") String category, @QueryParam("name") String name) {
        try {
            List<Product> products;

            if (category != null && !category.isEmpty()) {
                // Cerca i prodotti per categoria
                products = productRepository.findByCategory(category);
            } else if (name != null && !name.isEmpty()) {
                // Cerca i prodotti per nome
                products = productRepository.findBySimilarName(name);
            } else {
                // Restituisci tutti i prodotti se non ci sono parametri
                products = productRepository.findAllProducts();
            }

            return Response.status(Response.Status.OK).entity(products).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving products: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Context HttpHeaders httpHeaders, Product product) {
        try {
            // Prende il cookie di sessione
            Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
            if (sessionCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
            }

            // Prende l'utente dalla sessione
            Optional<Session> session=sessionRepository.findBySessionKey(sessionCookie.getValue());
            User user=session.get().getUser();

            // Verifica se l'utente è un admin
            if(user.getRole()!= User.Role.A){
                return Response.status(Response.Status.UNAUTHORIZED).entity("L'utente non ha il permesso di fare questa azione.").build();
            }

            // Verifica se esiste già un prodotto con il nome uguale
            if(productRepository.findByName(product.getName()) != null){
                return Response.status(Response.Status.BAD_REQUEST).entity("Prodotto con nome identico già presente").build();
            }

            // Crea il prodotto
            productRepository.addProduct(product);
            return Response.status(Response.Status.CREATED).entity("Product created successfully!").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create product: " + e.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
        public Response modify(@Context HttpHeaders httpHeaders, Product product){
        try {
            // Prende il cookie di sessione
            Cookie sessionCookie = httpHeaders.getCookies().get("SESSION_ID");
            if (sessionCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found. Please log in.").build();
            }

            // Prende l'utente dalla sessione
            Optional<Session> session=sessionRepository.findBySessionKey(sessionCookie.getValue());
            User user=session.get().getUser();

            // Verifica se l'utente è un admin
            if(user.getRole()!= User.Role.A){
                return Response.status(Response.Status.UNAUTHORIZED).entity("L'utente non ha il permesso di fare questa azione.").build();
            }

            // Recupera il prodotto esistente
            Product existingProduct = productRepository.findById(product.getId());
            if (existingProduct == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Prodotto non trovato").build();
            }

            // Aggiorna solo i campi non nulli
            if (product.getName() != null) existingProduct.setName(product.getName());
            if (product.getQuantity() != 0) existingProduct.setQuantity(product.getQuantity());
            if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
            if (product.getCategory() != null) existingProduct.setCategory(product.getCategory());

            // Aggiorna il prodotto nel db
            productRepository.updateProduct(existingProduct);
            return Response.status(Response.Status.CREATED).entity("Product updated successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update product: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@Context HttpHeaders httpHeaders, @PathParam("id") Long id) {
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

            // Verifica se l'utente è un admin
            if (user.getRole() != User.Role.A) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("L'utente non ha il permesso di fare questa azione.").build();
            }

            // Verifica se il prodotto esiste
            Product product = productRepository.findById(id);
            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Prodotto non trovato").build();
            }

            // Cancella il prodotto
            productRepository.deleteProduct(product);
            return Response.status(Response.Status.OK).entity("Product deleted successfully!").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete product: " + e.getMessage()).build();
        }
    }


}
