package trinity.play2learn.backend.user.services.user.interfaces;

public interface IUserValidateEqualsPasswordService {

    public boolean validate(String password, String dbPassword);
    
}
