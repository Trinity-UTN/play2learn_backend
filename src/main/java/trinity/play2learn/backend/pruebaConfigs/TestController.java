package trinity.play2learn.backend.pruebaConfigs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.ForbiddenException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.configs.response.ResponseFactory;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 👉 OK
    @GetMapping("/ok")
    public ResponseEntity<BaseResponse<String>> testOk() {
        return ResponseFactory.ok("Todo correcto", "Respuesta OK");
    }

    // ✅ CREATED
    @PostMapping("/created")
    public ResponseEntity<BaseResponse<String>> testCreated() {
        return ResponseFactory.created("Recurso creado", "Respuesta CREATED");
    }

    // 🙌 ACCEPTED
    @PostMapping("/accepted")
    public ResponseEntity<BaseResponse<String>> testAccepted() {
        return ResponseFactory.accepted("Procesando tarea", "Respuesta ACCEPTED");
    }

    // 😶 NO CONTENT
    @DeleteMapping("/no-content")
    public ResponseEntity<BaseResponse<Void>> testNoContent() {
        return ResponseFactory.noContent("Recurso eliminado con éxito");
    }

    // 📄 PAGINATED
    @GetMapping("/paginated")
    public ResponseEntity<BaseResponse<PaginatedData<String>>> testPaginated() {
        PaginatedData<String> paginated = PaginatedData.<String>builder()
        .results(List.of("Uno", "Dos", "Tres"))
        .count(3)
        .pageSize(3)
        .currentPage(1)
        .totalPages(1)
        .build();

        return ResponseFactory.paginated(paginated, "Resultados paginados");
    }

    // 🚨 BAD REQUEST
    @GetMapping("/bad-request")
    public void throwBadRequest() {
        throw new BadRequestException("Esto es un error 400");
    }

    // 🚫 UNAUTHORIZED
    @GetMapping("/unauthorized")
    public void throwUnauthorized() {
        throw new UnauthorizedException("No tenés permisos");
    }

    // ❌ FORBIDDEN
    @GetMapping("/forbidden")
    public void throwForbidden() {
        throw new ForbiddenException("Acceso prohibido");
    }

    // 🕳️ NOT FOUND
    @GetMapping("/not-found")
    public void throwNotFound() {
        throw new NotFoundException("No se encontró el recurso");
    }

    // ⚔️ CONFLICT
    @GetMapping("/conflict")
    public void throwConflict() {
        throw new ConflictException("Conflicto detectado");
    }

}