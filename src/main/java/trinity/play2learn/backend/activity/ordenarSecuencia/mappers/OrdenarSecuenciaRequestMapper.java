package trinity.play2learn.backend.activity.ordenarSecuencia.mappers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.EventRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.utils.DtoValidator;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class OrdenarSecuenciaRequestMapper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para usar ISO-8601

    public static OrdenarSecuenciaRequestDto toRequestDto(String payloadJson, List<MultipartFile> images) throws IOException {
        OrdenarSecuenciaRequestDto dto = mapper.readValue(
            payloadJson,
            OrdenarSecuenciaRequestDto.class
        );

        Map<String, MultipartFile> imagenesMap = Optional.ofNullable(images)
            .orElse(List.of()).stream()
            .collect(Collectors.toMap(MultipartFile::getName, Function.identity()));

        for (EventRequestDto evento : dto.getEvents()) {
            String key = String.valueOf(evento.getOrder());  // Usamos el número de orden como clave
            MultipartFile imagen = imagenesMap.get(key);
            if (imagen != null && !imagen.isEmpty()) {
                evento.setImage(imagen);
            }
        }

        DtoValidator.validate(dto);

        return dto;
    }
}