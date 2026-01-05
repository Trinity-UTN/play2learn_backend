package trinity.play2learn.backend.configs.fileUpload.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.fileUpload.dtos.FileDownloadData;
import trinity.play2learn.backend.configs.fileUpload.mappers.StoredFileMapper;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IDownloadFromUploadCareService;

@Service
@AllArgsConstructor
public class DownloadFromUploadCareService implements IDownloadFromUploadCareService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public FileDownloadData downloadFileFromUploadCare(String fileName, String cdnUrl) {

        try {
            // Peticion GET a UploadCare a traves de la URL publica (cdnUrl)
            // Trae el archivo como un array de Bytes
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    cdnUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Fallo en la descarga: Uploadcare devolvió un error: " + response.getBody());
            }

            byte[] fileBytes = response.getBody();

            if (fileBytes == null || fileBytes.length == 0) {
                throw new RuntimeException("Fallo en la descarga: El archivo descargado está vacío");
            }

            // Envuelvo el array de bytes en un objeto me permite manejarlo como un recurso
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            String contentType = response.getHeaders()
                    .getContentType() != null
                            ? response.getHeaders().getContentType().toString()
                            : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return StoredFileMapper.toFileDownloadData(fileName, contentType, resource);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al conectar con Uploadcare CDN", e);
        }
    }
}
