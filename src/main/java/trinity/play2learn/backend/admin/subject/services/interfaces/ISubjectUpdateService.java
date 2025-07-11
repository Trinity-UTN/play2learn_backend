package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;

public interface ISubjectUpdateService {

    SubjectResponseDto cu29UpdateSubject(SubjectUpdateRequestDto subjectDto);
}
