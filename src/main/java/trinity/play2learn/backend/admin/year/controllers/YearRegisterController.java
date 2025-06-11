package trinity.play2learn.backend.admin.year.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.services.YearRegisterService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

/**
 * Controlador REST para gestionar los años académicos del sistema.
 * Expone endpoints para la creación de nuevos años.
 */
@RequestMapping("/admin/years")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class YearRegisterController {
    
    private final YearRegisterService yearRegisterService;

    public YearRegisterController(YearRegisterService yearRegisterService) {
        this.yearRegisterService = yearRegisterService;
    }

    /**
     * CU7 - Crear un nuevo año académico.
     *
     * @param YearRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el año creado y mensaje de éxito.
     */
    @PostMapping
    public ResponseEntity<BaseResponse<YearResponseDto>> create(@Valid @RequestBody YearRequestDto yearDto, BindingResult result) {
        return ResponseFactory.created(yearRegisterService.cu7RegisterYear(yearDto, result), "Created succesfully");
    }
}
