package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaValidationsService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
public class NoLudicaValidationsService implements INoLudicaValidationsService{

    @Override
    public void validateNoLudicaCompleted(String plaintext, MultipartFile file) {
        
        if ((file == null || file.isEmpty()) && (plaintext.length() == 0 || plaintext == null)) {
            throw new BadRequestException("El texto y el archivo no pueden estar vacios");
            
        }

        if (plaintext.length() > 1000) {
            throw new BadRequestException("El texto no puede tener mas de 1000 caracteres");
        }
    }
    
    
}
