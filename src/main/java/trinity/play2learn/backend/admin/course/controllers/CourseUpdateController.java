package trinity.play2learn.backend.admin.course.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.dtos.CourseUpdateDto;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseUpdateService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/courses")
public class CourseUpdateController {
    
    private final ICourseUpdateService courseUpdateService;

    @PutMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<CourseResponseDto>> update(@PathVariable Long id, @Valid @RequestBody CourseUpdateDto courseDto) {
        return ResponseFactory.ok(
            courseUpdateService.cu14UpdateCourse(id, courseDto),
            SuccessfulMessages.updatedSuccessfully("Curso")
        );
    }
}
