package trinity.play2learn.backend.notification.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCheckByActivitiesService;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateByUsersService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class NotificationCheckActivityService implements INotificationCheckByActivitiesService {

    private final IActivityRepository activityRepository;
    private final INotificationCreateByUsersService createUsersNotifications;

    @Override
    @Scheduled(cron = "0 30 1 * * ?") // Cada dia a las 1:30
    @Transactional
    public void cu116CheckActivityNotifications() {

        List<Activity> newActivitiesPublished = activityRepository
                .findAllByStartDateBetweenAndDeletedAtIsNull(LocalDateTime.now().minusHours(24), LocalDateTime.now());

        List<Activity> activitiesAboutToExpire = activityRepository
                .findAllByEndDateBetweenAndDeletedAtIsNull(LocalDateTime.now(), LocalDateTime.now().plusHours(24));

        // Si existe una actividad que comenzo en las ultimas 24 horas, se crea una
        // notificacion
        if (!newActivitiesPublished.isEmpty()) {

            newActivitiesPublished.forEach(activity -> {

                List<User> users = activity.getSubject().getStudents().stream().map(Student::getUser).toList();

                createUsersNotifications.createUsersNotifications(users,
                        NotificationType.NEW_ACTIVITY_PUBLISHED);
            });
        }

        if (!activitiesAboutToExpire.isEmpty()) {
            activitiesAboutToExpire.forEach(activity -> {
                List<User> users = activity.getSubject().getStudents().stream().map(Student::getUser).toList();
                createUsersNotifications.createUsersNotifications(users,
                        NotificationType.ACTIVITY_ABOUT_TO_EXPIRE);
            });
        }
    }
}
