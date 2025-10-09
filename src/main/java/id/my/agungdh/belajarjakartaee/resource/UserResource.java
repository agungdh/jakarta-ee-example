package id.my.agungdh.belajarjakartaee.resource;

import id.my.agungdh.belajarjakartaee.dto.UserDTO;
import id.my.agungdh.belajarjakartaee.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // <-- penting untuk proxy CDI
public class UserResource {

    @Inject
    UserService service;

    @GET
    public List<UserDTO> getAll() { return service.getAll(); }

    @GET @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var user = service.getById(id);
        return (user == null)
                ? Response.status(Response.Status.NOT_FOUND).entity(Map.of("error","User not found")).build()
                : Response.ok(user).build();
    }

    @POST
    public Response create(@Valid UserDTO dto) {
        var saved = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UserDTO dto) {
        var updated = service.update(id, dto);
        return (updated == null)
                ? Response.status(Response.Status.NOT_FOUND).entity(Map.of("error","User not found")).build()
                : Response.ok(updated).build();
    }

    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).entity(Map.of("error","User not found")).build();
    }
}
