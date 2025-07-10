package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;

public interface ISubjectRegisterService {
    
    SubjectResponseDto cu28RegisterSubject(SubjectRequestDto subjectDto);
}
