package trinity.play2learn.backend.admin.course.services.interfaces;

import trinity.play2learn.backend.admin.year.models.Year;

public interface ICourseExistByService {
    
    public boolean validate(String name, Year year);

    public boolean validate(Long id);

    public boolean validate(String name);
    
    public void validateExceptId(Long id, String name, Year year); 
}
