package trinity.play2learn.backend.admin.course.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/courses")
@RestController
@AllArgsConstructor
public class CourseGetController {
    
    private final ICourseGetService courseGetService;

    @GetMapping ("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<CourseResponseDto>> get(@PathVariable Long id){
        return ResponseFactory.ok(
            courseGetService.cu17GetCourse(id), 
            SuccesfullyMessages.okSuccessfully()
        );
    }

}
