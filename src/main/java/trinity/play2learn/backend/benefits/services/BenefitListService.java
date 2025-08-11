package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitListService implements IBenefitListService {
    
    private final IBenefitRepository benefitRepository;
    private final ITeacherGetByEmailService teacherGetByEmailService;

    @Override
    public List<BenefitResponseDto> cu55ListBenefits(User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        return BenefitMapper.toListDto(benefitRepository.findAllBySubjectTeacher(teacher));

    }
    
}
