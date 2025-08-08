package trinity.play2learn.backend.profile.avatar.controllers;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.profile.avatar.dtos.request.AspectRegisterRequestDto;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectRegisterService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.utils.DtoValidator;

@RestController
@AllArgsConstructor
@RequestMapping("profile/avatar/aspect")
public class AspectRegisterController {

    private final IAspectRegisterService aspectRegisterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SessionRequired (roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<AspectResponseDto>> register(
        @RequestParam String name,
        @RequestParam String price,
        @RequestParam String type,
        @RequestParam MultipartFile image
    ) throws BadRequestException, IOException {
        AspectRegisterRequestDto dto = AspectMapper.toRequestDto(name, image, new BigDecimal(price), type);
        DtoValidator.validate(dto);
        return ResponseFactory.created(
            aspectRegisterService.cu47registerAspect(dto),
            SuccessfulMessages.createdSuccessfully("Aspecto")
        );
    }
}
