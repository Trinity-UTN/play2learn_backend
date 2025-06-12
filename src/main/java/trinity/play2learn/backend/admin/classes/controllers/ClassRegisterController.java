package trinity.play2learn.backend.admin.classes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.classes.dtos.ClassRequestDto;
import trinity.play2learn.backend.admin.classes.dtos.ClassResponseDto;
import trinity.play2learn.backend.admin.classes.services.ClassRegisterService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RequestMapping("/admin/classes")
@RestController
@AllArgsConstructor
public class ClassRegisterController {
    
    private final ClassRegisterService classRegisterService;

    /**
     * CU6 - Crear un nuevo curso.
     *
     * @param ClassRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el curso creado y mensaje de éxito.
     */
    @PostMapping
    public ResponseEntity<BaseResponse<ClassResponseDto>> create(@Valid @RequestBody ClassRequestDto classDto) {
        return ResponseFactory.created(classRegisterService.cu6RegisterClass(classDto), "Created succesfully");
    }
}
