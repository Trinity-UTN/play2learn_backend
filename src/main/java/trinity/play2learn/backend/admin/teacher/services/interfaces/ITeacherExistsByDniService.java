package trinity.play2learn.backend.admin.teacher.services.interfaces;

public interface ITeacherExistsByDniService {
    
    void validate(String dni, Long id);
    void validate(String dni);
}
