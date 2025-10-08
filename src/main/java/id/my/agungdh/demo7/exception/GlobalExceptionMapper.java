package id.my.agungdh.demo7.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Menangani semua exception global dan ubah ke JSON yang rapi.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        // --- Validasi DTO ---
        if (e instanceof ConstraintViolationException ve) {
            var violations = ve.getConstraintViolations().stream()
                    .map(v -> Map.of(
                            "property", v.getPropertyPath().toString(),
                            "message", v.getMessage()))
                    .collect(Collectors.toList());
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Validation failed");
            body.put("violations", violations);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(body)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // --- Constraint / Duplicate dari DB ---
        if (e.getCause() instanceof SQLException sqlEx &&
                sqlEx.getMessage() != null &&
                sqlEx.getMessage().toLowerCase().contains("duplicate")) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Conflict", "message", "Duplicate key violation"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // --- Runtime exception custom (mis. "Email already exists") ---
        if (e instanceof RuntimeException re &&
                re.getMessage() != null &&
                re.getMessage().contains("exists")) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Conflict", "message", re.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // --- Default (500) ---
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getClass().getSimpleName());
        error.put("message", e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
