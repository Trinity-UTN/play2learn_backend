package trinity.play2learn.backend.activity.activity.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IActivityRepository extends CrudRepository<Activity, Long> {
    
    List<Activity> findAllBySubjectInAndDeletedAtIsNull(List<Subject> subjects);

    List<Activity> findAllByStartDateBetweenAndDeletedAtIsNull(LocalDateTime startPeriod, LocalDateTime endPeriod);

    List<Activity> findAllByEndDateBetweenAndDeletedAtIsNull(LocalDateTime startPeriod, LocalDateTime endPeriod);  
}
