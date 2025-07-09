package trinity.play2learn.backend.admin.year.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByYearService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearDeleteService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class YearDeleteService implements IYearDeleteService{

    private final IYearGetByIdService yearGetByIdService;

    private final ICourseExistByYearService courseExistByYearService;

    private final IYearRepository yearRepository;

    @Override
    public void cu11deleteYear(String id) {
        Long yearId;
        try {
            yearId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid year ID format: " + id);
        }
        
        Year year = yearGetByIdService.get(yearId);

        if (year.getDeletedAt() != null) {
            throw new BadRequestException("Year with ID " + id + " is already deleted.");
        }

        if (courseExistByYearService.validate(year)) {
            throw new ConflictException("Cannot delete year with ID " + id + " because it has associated courses.");
        }

        year.delete();

        yearRepository.save(year);
        
    }
    
}
