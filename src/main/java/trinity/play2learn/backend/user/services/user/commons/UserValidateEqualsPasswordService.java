package trinity.play2learn.backend.user.services.user.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.services.user.interfaces.IUserValidateEqualsPasswordService;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@AllArgsConstructor
public class UserValidateEqualsPasswordService implements IUserValidateEqualsPasswordService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean validate(String password, String dbPassword) {
        return passwordEncoder.matches(password, dbPassword);
    }
}
