package trinity.play2learn.backend.user.services.user.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistsByEmailService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistService;

@Service
@AllArgsConstructor
public class UserExistsByEmailService implements IUserExistsByEmailService{
    
    private final IUserExistService userExistService;

    @Override
    public void validateIfUserIsActive(String email) {
        if (!userExistService.validate(email)) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.UNAUTHORIZED
            );
        };
    }
}
