package trinity.play2learn.backend.activity.memorama.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.mappers.MemoramaRequestMapper;
import trinity.play2learn.backend.activity.memorama.services.interfaces.IMemoramaGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("activities/memorama")
public class MemoramaRegisterController {
    
    private final IMemoramaGenerateService memoramaRegisterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SessionRequired (roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<MemoramaResponseDto>> register(
        @RequestParam String description,
        @RequestParam LocalDateTime startDate,
        @RequestParam LocalDateTime endDate,
        @RequestParam Difficulty difficulty,
        @RequestParam int maxTime,
        @RequestParam Long subjectId,
        @RequestParam int attempts,
        @RequestParam Double initialBalance,
        @RequestParam List<String> concepts,
        @RequestParam List<MultipartFile> images
    ) throws BadRequestException, IOException {
        return ResponseFactory.created(
            memoramaRegisterService.cu41GenerateMemorama(MemoramaRequestMapper.toDto(
            description,
            startDate,
            endDate,
            difficulty,
            maxTime,
            subjectId,
            attempts,
            concepts,
            images,
            initialBalance
        )),
            SuccessfulMessages.createdSuccessfully("Actividad de Memorama")
        );
    }
}
