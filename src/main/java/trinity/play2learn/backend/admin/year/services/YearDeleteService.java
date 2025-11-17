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
import trinity.play2learn.backend.configs.messages.BadRequestExceptionMessages;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

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
            throw new BadRequestException(
                BadRequestExceptionMessages.invalidFormat(id)
            );
        }
        
        Year year = yearGetByIdService.findById(yearId);

        if (year.getDeletedAt() != null) {
            throw new BadRequestException(
                ConflictExceptionMessages.resourceAlreadyDeleted("Año", id)
            );
        }

        if (courseExistByYearService.validate(year)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceDeletionNotAllowedDueToAssociations(
                    "Año", 
                    id, 
                    "Cursos"
                )
            );
        }

        year.delete();

        yearRepository.save(year);
        
    }
    
}
