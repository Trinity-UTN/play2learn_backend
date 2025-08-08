package trinity.play2learn.backend.benefits.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.benefits.dtos.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGenerateService;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitGenerateService implements IBenefitGenerateService{
    
    private final IBenefitRepository benefitRepository;
    private final ISubjectGetByIdService subjectGetService;

    @Override
    @Transactional
    public BenefitResponseDto cu51GenerateBenefit(BenefitRequestDto benefitDto , User user) {
        
        Subject subject = subjectGetService.findById(benefitDto.getSubjectId()); 

        //Valida que el docente este asignado a la materia sobre la cual quiere crear el beneficio
        if (!subject.hasTeacherByEmail(user.getEmail())) {
            throw new UnauthorizedException(UnauthorizedExceptionMessages.BENEFIT_UNAUTHORIZED_TEACHER);
        }

        Benefit benefit = BenefitMapper.toModel(benefitDto, subject); 

        return BenefitMapper.toDto(benefitRepository.save(benefit)); 
    }

    
}
