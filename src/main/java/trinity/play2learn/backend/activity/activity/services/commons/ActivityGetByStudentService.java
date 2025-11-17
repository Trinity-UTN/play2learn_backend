package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByStudentService;

@Service
@AllArgsConstructor
public class ActivityGetByStudentService implements IActivityGetByStudentService {
    
    private final ISubjectGetByStudentService subjectGetByStudentService;
    private final IActivityRepository activityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getByStudent(Student student) {

        List<Subject> subjects = subjectGetByStudentService.getByStudent(student);

        List<Activity> activities = activityRepository.findAllBySubjectInAndDeletedAtIsNull(subjects);

        return activities;
    }
    
}
