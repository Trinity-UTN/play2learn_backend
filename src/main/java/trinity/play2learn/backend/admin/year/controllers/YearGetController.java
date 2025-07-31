package trinity.play2learn.backend.admin.year.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.services.YearGetService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/years")
@RestController
@AllArgsConstructor
public class YearGetController {

    private final YearGetService yearGetService;

    /**
     * CU7 - Crear un nuevo año académico.
     *
     * @param YearRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el año creado y mensaje de éxito.
     */
    @GetMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<YearResponseDto>> get(@PathVariable Long id) {
        return ResponseFactory.ok(yearGetService.cu13GetYear(id), SuccesfullyMessages.okSuccessfully());
    }
}
