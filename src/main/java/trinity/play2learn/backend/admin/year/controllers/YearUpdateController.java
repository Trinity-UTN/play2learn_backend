package trinity.play2learn.backend.admin.year.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.dtos.YearUpdateRequestDto;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearUpdateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

/**
 * Controlador REST para gestionar los años académicos del sistema.
 * Expone endpoints para la actualización de nuevos años.
 */
@RequestMapping("/admin/years")
@RestController
@AllArgsConstructor
public class YearUpdateController {

    private final IYearUpdateService yearUpdateService;

    /**
     * CU10 - Modifica un año académico existente.
     *
     * @param YearRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el año creado y mensaje de éxito.
     */
    @PutMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<YearResponseDto>> update(@PathVariable Long id, @Valid @RequestBody YearUpdateRequestDto yearDto) {
        return ResponseFactory.ok(yearUpdateService.cu10UpdateYear(id, yearDto), SuccessfulMessages.updatedSuccessfully("Año"));
    }
    
}
