package cest.la.vie.rest;

import cest.la.vie.persistence.IngredientRepository;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ingredients")
public class IngredientResource {

    private final IngredientRepository ingredientRepository;

    public IngredientResource(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        return Response.status(Response.Status.OK).entity(ingredientRepository.getAllIngredients()).build();
    }
}
