package trinity.play2learn.backend.configs.uploadCare.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadcareService {

    @Value("${UPLOAD_CARE_API_KEY}")
    private String publicKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> uploadToUploadcare(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String uploadUrl = "https://upload.uploadcare.com/base/";

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("UPLOADCARE_PUB_KEY", publicKey);
            body.add("UPLOADCARE_STORE", 1);

            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileAsResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> resp = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            if (resp.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new RuntimeException("Fallo en la subida: Uploadcare devolvió un error: " + resp.getBody());
            } else if (!resp.getStatusCode().equals(HttpStatus.OK)) {
                throw new RuntimeException("Fallo en la subida: Uploadcare devolvió un error: " + resp.getBody());
            }

            // La respuesta puede venir en formatos variados: {"file":"uuid"} o {"myfile.jpg":"uuid"}
            JsonNode root = objectMapper.readTree(resp.getBody());

            String uuid = null;
            if (root.has("file")) {
                uuid = root.get("file").asText();
            } else {
                Iterator<String> it = root.fieldNames();
                if (it.hasNext()) {
                    String first = it.next();
                    JsonNode val = root.get(first);
                    uuid = val.isTextual() ? val.asText() : val.toString();
                }
            }

            if (uuid == null) {
                throw new RuntimeException("No se recibió UUID del uploadcare: " + resp.getBody());
            }

            String cdnUrl = "https://ucarecdn.com/" + uuid + "/";

            Map<String, String> result = new HashMap<>();
            result.put("fileName", file.getOriginalFilename());
            result.put("uuid", uuid);
            result.put("cdnUrl", cdnUrl);
            result.put("rawResponse", resp.getBody());
            return result;

        } catch (RestClientException e) {
            throw new RuntimeException("Error de conexión con Uploadcare: " + e.getMessage(), e);
        } catch (Exception e) {
            
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
        }
    }
}