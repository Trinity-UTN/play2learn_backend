package trinity.play2learn.backend.activity.ordenarSecuencia.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.mappers.OrdenarSecuenciaRequestMapper;
import trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces.IOrdenarSecuenciaActivityGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/activities/ordenar-secuencia")
public class OrdenarSecuenciaGenerateController {

    private final IOrdenarSecuenciaActivityGenerateService ordenarSecuenciaGenerateService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SessionRequired (roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<OrdenarSecuenciaResponseDto>> generate(
        @RequestParam("payload") String payloadJson,
        @RequestParam MultiValueMap<String, MultipartFile> files
    ) throws IOException {

        List<MultipartFile> images = files.values().stream()
            .flatMap(Collection::stream)
        .collect(Collectors.toList());

        OrdenarSecuenciaRequestDto dto = OrdenarSecuenciaRequestMapper.toRequestDto(payloadJson, images);
        
        return ResponseFactory.created(
            ordenarSecuenciaGenerateService.cu44GenerateOrdenarSecuencia(dto),
            SuccessfulMessages.createdSuccessfully("Actividad de ordenar secuencia")
        );
    }
      
}
