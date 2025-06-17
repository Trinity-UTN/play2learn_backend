package trinity.play2learn.backend.user.services.user.interfaces;

public interface IUserExistService {

    public boolean validate(String email); // Validates if a user with the given email already exists in the database.
    
} 
