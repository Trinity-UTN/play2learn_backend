package trinity.play2learn.backend.admin.course.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseDeleteService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/courses")
public class CourseDeleteController {
    
    private final ICourseDeleteService courseDeleteService;

    @DeleteMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        courseDeleteService.cu15DeleteCourse(id);
        return ResponseFactory.noContent("Deleted successfully");
    }
}
