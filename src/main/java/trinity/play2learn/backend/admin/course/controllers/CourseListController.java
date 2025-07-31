package trinity.play2learn.backend.admin.course.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.services.CourseListService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/courses")
@RestController
@AllArgsConstructor
public class CourseListController {
    
    private final CourseListService courseListService;

    /**
     * CU6 - Crear un nuevo curso.
     *
     * @param CourseRequestDto yearDto Datos del año académico.
     * @return ResponseEntity con el curso creado y mensaje de éxito.
     */
    @GetMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<List<CourseResponseDto>>> register() {
        return ResponseFactory.ok(courseListService.cu9ListCourses(), "ok");
    }
}
