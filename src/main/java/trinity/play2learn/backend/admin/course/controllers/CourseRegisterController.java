package trinity.play2learn.backend.admin.course.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.services.CourseRegisterService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/courses")
@RestController
@AllArgsConstructor
public class CourseRegisterController {
    
    private final CourseRegisterService courseRegisterService;

    /**
     * CU6 - Crear un nuevo curso.
     *
     * @param CourseRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el curso creado y mensaje de éxito.
     */
    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<CourseResponseDto>> register(@Valid @RequestBody CourseRequestDto courseDto) {
        return ResponseFactory.created(
            courseRegisterService.cu6RegisterCourse(courseDto),
            SuccessfulMessages.createdSuccessfully("Curso")
        );
    }
}
