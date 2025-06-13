package trinity.play2learn.backend.admin.classes.services.interfaces;

import trinity.play2learn.backend.admin.year.models.Year;

public interface IClassExistService {
    
    public boolean validate(String name, Year year);

    public boolean validate(Long id);

    public boolean validate(String name);
    
}
