package cest.la.vie.rest;

import cest.la.vie.persistence.IngredientRepository;
import cest.la.vie.persistence.ProductHasIngredientRepository;
import cest.la.vie.persistence.ProductRepository;
import cest.la.vie.persistence.SessionRepository;
import cest.la.vie.persistence.model.*;
import cest.la.vie.rest.model.ProductRequest;
import cest.la.vie.rest.model.ProductResponse;
import cest.la.vie.service.ProductService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;

@Path("/product")
public class ProductResource {

    private final ProductRepository productRepository;
    private final SessionRepository sessionRepository;

    private final ProductHasIngredientRepository productHasIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductService productService;

    public ProductResource(ProductRepository productRepository, SessionRepository sessionRepository, ProductHasIngredientRepository productHasIngredientRepository, IngredientRepository ingredientRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.sessionRepository = sessionRepository;
        this.productHasIngredientRepository = productHasIngredientRepository;
        this.ingredientRepository = ingredientRepository;

        this.productService = productService;
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

            // Mappa per organizzare i prodotti per categoria
            Map<String, Map<String, Object>> categoryMap = new LinkedHashMap<>();

            for (Product product : products) {
                List<Ingredient> ingredients = productHasIngredientRepository.findIngredientsByProduct(product);
                ProductResponse response = new ProductResponse(product, ingredients);

                String categoryName = product.getCategory().getName();
                if (!categoryMap.containsKey(categoryName)) {
                    // Crea una nuova categoria se non esiste
                    Map<String, Object> categoryDetails = new LinkedHashMap<>();
                    categoryDetails.put("id", product.getCategory().getId());
                    categoryDetails.put("name", categoryName);
                    categoryDetails.put("products", new ArrayList<ProductResponse>());
                    categoryMap.put(categoryName, categoryDetails);
                }

                // Aggiungi il prodotto alla lista della categoria
                List<ProductResponse> productList = (List<ProductResponse>) categoryMap.get(categoryName).get("products");
                productList.add(response);
            }

            // Converte la mappa in una lista finale
            List<Map<String, Object>> finalResponse = new ArrayList<>(categoryMap.values());

            return Response.status(Response.Status.OK).entity(finalResponse).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving products: " + e.getMessage())
                    .build();
        }
    }


    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id){
        Product product = productRepository.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }
        List<Ingredient> ingredients = productHasIngredientRepository.findIngredientsByProduct(product);
        ProductResponse response= new ProductResponse(product,ingredients);
        return Response.ok(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Context HttpHeaders httpHeaders, ProductRequest product) {
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

            //converte il productRequest in product
            Product p=productService.convertToProduct(product);
            // Crea il prodotto
            productRepository.addProduct(p);
            return Response.status(Response.Status.CREATED).entity("Product created successfully!").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create product: " + e.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
        public Response modify(@Context HttpHeaders httpHeaders, ProductRequest productRequest){
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

            //converte il productRequest in product
            Product product=productService.convertToProduct(productRequest);

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

    // @POST per aggiungere un ingrediente a un prodotto
    @POST
    @Path("/{id}/addIngredient/{ingredientId}")
    public Response addIngredientToProduct(@PathParam("id") Long productId, @PathParam("ingredientId") Long ingredientId) {
        Product product = Product.findById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }

        Ingredient ingredient = ingredientRepository.findById(ingredientId);
        if (ingredient == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Ingredient not found").build();
        }

        // Crea una relazione ProductHasIngredient
        ProductHasIngredient productHasIngredient = new ProductHasIngredient();
        productHasIngredient.setProduct(product);
        productHasIngredient.setIngredient(ingredient);

        productHasIngredientRepository.addIngredientToProduct(productHasIngredient);
        return Response.status(Response.Status.CREATED).entity(productHasIngredient).build();
    }

    // @DELETE per rimuovere un ingrediente da un prodotto
    @DELETE
    @Path("{id}/removeIngredient/{ingredientId}")
        public Response deleteIngredientFromProduct(@PathParam("id") Long productId, @PathParam("ingredientId") Long ingredientId) {
        Product product = Product.findById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }

        Ingredient ingredient = ingredientRepository.findById(ingredientId);
        if (ingredient == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Ingredient not found").build();
        }

        productHasIngredientRepository.removeIngredientFromProduct(product, ingredient);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
