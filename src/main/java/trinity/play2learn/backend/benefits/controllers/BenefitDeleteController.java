package trinity.play2learn.backend.benefits.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitDeleteService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/benefits/teacher")
public class BenefitDeleteController {
    
    private final IBenefitDeleteService benefitDeleteService;

    @DeleteMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<Void>> deleteBenefit(@SessionUser User user, @PathVariable Long id) {
        
        benefitDeleteService.cu94DeleteBenefit(user, id);
        return ResponseFactory.noContent("Beneficio eliminado con exito");
    }
}
