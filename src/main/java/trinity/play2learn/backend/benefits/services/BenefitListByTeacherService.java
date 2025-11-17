package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByTeacherService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitListByTeacherService implements IBenefitListByTeacherService {
    
    private final IBenefitRepository benefitRepository;
    private final ITeacherGetByEmailService teacherGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponseDto> cu55ListBenefitsByTeacher(User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        return BenefitMapper.toListDto(benefitRepository.findAllBySubjectTeacherAndDeletedAtIsNull(teacher));

    }
    
}
