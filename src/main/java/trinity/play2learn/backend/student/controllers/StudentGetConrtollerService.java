package trinity.play2learn.backend.student.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.student.services.interfaces.IStudentGetByTokenService;
import trinity.play2learn.backend.user.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentGetConrtollerService {
    
    private final IStudentGetByTokenService studentGetByTokenService;

    @GetMapping
    public ResponseEntity<BaseResponse<StudentResponseDto>> getStudent(@SessionUser User user) {
        return ResponseFactory.ok(studentGetByTokenService.cu71GetStudentByToken(user),SuccessfulMessages.okSuccessfully());
    }
    
}
