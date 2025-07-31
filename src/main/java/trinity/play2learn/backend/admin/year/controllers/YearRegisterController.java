package trinity.play2learn.backend.admin.year.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.services.YearRegisterService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

/**
 * Controlador REST para gestionar los años académicos del sistema.
 * Expone endpoints para la creación de nuevos años.
 */
@RequestMapping("/admin/years")
@RestController
@AllArgsConstructor
public class YearRegisterController {
    
    private final YearRegisterService yearRegisterService;

    /**
     * CU7 - Crear un nuevo año académico.
     *
     * @param YearRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el año creado y mensaje de éxito.
     */
    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<YearResponseDto>> register(@Valid @RequestBody YearRequestDto yearDto) {
        return ResponseFactory.created(yearRegisterService.cu7RegisterYear(yearDto), "Created succesfully");
    }
}
