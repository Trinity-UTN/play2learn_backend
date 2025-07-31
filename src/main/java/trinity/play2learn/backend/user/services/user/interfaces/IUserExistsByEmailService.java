package trinity.play2learn.backend.user.services.user.interfaces;

public interface IUserExistsByEmailService {
    void validateIfUserIsActive(String email);
}
