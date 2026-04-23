package trinity.play2learn.backend.admin.year.services;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByYearService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearDeleteService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;

@Service
@AllArgsConstructor
public class YearDeleteService implements IYearDeleteService{

    private final IYearGetByIdService yearGetByIdService;

    private final ICourseExistByYearService courseExistByYearService;

    private final IYearRepository yearRepository;

    @Override
    public void cu11deleteYear(Long id) {
        
        Year year = yearGetByIdService.findById(id);

        //valida si el año tiene cursos asociados y lanza un 409 si los tiene
        courseExistByYearService.validate(year);

        //Eliminado fisico del año
        yearRepository.delete(year);
    }
    
}
