package trinity.play2learn.backend.configs.imgBB.services;

import lombok.RequiredArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBResponseDTO;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBUploadResultDTO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    @Value("${IMG_BB_API_KEY}")
    private String imgbbApiKey;


    public ImgBBUploadResultDTO uploadImage(MultipartFile image) throws IOException {
        if (imgbbApiKey == null || imgbbApiKey.isBlank()) {
            throw new IllegalStateException("IMGBB_API_KEY no está configurada");
        }

        String uploadUrl = "https://api.imgbb.com/1/upload?key=" + imgbbApiKey;
        
        String encodedImage = Base64.encodeBase64String(image.getBytes());
        String encodedParam = URLEncoder.encode(encodedImage, "UTF-8");

        String body = "image=" + encodedParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(body, headers);


        ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ImgBBResponseDTO imgbbResponse = objectMapper.readValue(response.getBody(), ImgBBResponseDTO.class);

            if (imgbbResponse.isSuccess()) {
                return new ImgBBUploadResultDTO(
                    imgbbResponse.getData().getUrl(),
                    imgbbResponse.getData().getDeleteUrl()
                );
            } else {
                throw new BadRequestException("Fallo en la subida: ImgBB devolvió success = false");
            }
        } else {
            throw new BadRequestException("Error al subir imagen: código " + response.getStatusCode());
        }
    }

}
