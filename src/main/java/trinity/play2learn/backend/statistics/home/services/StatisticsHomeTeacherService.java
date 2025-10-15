package trinity.play2learn.backend.statistics.home.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateTotalRealizationsService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityDataDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeTeacherResponseDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsSubjectDataDto;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsActivityDataMapper;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsHomeTeacherMapper;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsSubjectDataMapper;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeTeacherService;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsTotalCoursesByTeacherService;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsTotalStudentsByTeacherService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StatisticsHomeTeacherService implements IStatisticsHomeTeacherService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;

    private final ISubjectRepository subjectRepository;

    private final IStatisticsTotalStudentsByTeacherService statisticsTotalStudentsByTeacherService;

    private final IActivityRepository activityRepository;

    private final IStatisticsTotalCoursesByTeacherService statisticsTotalCoursesByTeacherService;

    private final IBenefitRepository benefitRepository;

    private final IActivityCalculateTotalRealizationsService activityCalculateTotalRealizationsService;

    @Override
    @Transactional(readOnly = true)
    public StatisticsHomeTeacherResponseDto cu68GetStatisticsHomeTeacher(User user) {
        /**
         * Datos a mostrar en el home:
            Cantidad de estudiantes total----
            Cantidad de actividades creadas---
            Cantidad de cursos en los que esta asignado---
            Cantidad de beneficios canjeados---
            Listado de sus materias con:
            cantidad de estudiantes por materia
            cantidad de actividades creadas por materia
            Ultimas 3 actividades creadas con:
            cantidad de estudiantes que la han hecho
            el limite de uso
            hace cuanto tiempo se creo
            Top 3 de beneficios mas usados (Si les parece)
         */

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        List<Subject> subjects = subjectRepository.findByTeacher(teacher);

        List<Activity> activities = activityRepository.findAllBySubjectInAndDeletedAtIsNull(subjects);

        List<Benefit> benefits = benefitRepository.findAllBySubjectTeacher(teacher);

        //Cantidad de Estudiantes total
        int totalStudents = statisticsTotalStudentsByTeacherService.execute(subjects);

        //Cantidad de cursos en los que esta asignado
        int totalCourses = statisticsTotalCoursesByTeacherService.execute(subjects);

        //Cantidad de actividades creadas
        int totalActivities = activities.size();

        //Cantidad de beneficios creados
        int totalBenefits = benefits.size();

        //Listado de sus materias con:
        //cantidad de estudiantes por materia
        //cantidad de actividades creadas por materia

        List<StatisticsSubjectDataDto> subjectsData = subjects.stream()
                .map(subject -> calculate(subject, activities))
                .toList();

        //Ultimas 3 actividades creadas con:
        //cantidad de estudiantes que la han hecho
        //el limite de uso
        //hace cuanto tiempo se creo

        List<StatisticsActivityDataDto> lastActivitiesData = calculate(activities.stream()
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .limit(3)
                .toList()
        );


        return StatisticsHomeTeacherMapper.toDto(
            totalStudents, 
            totalActivities,
            totalCourses, 
            totalBenefits, 
            subjectsData, 
            lastActivitiesData
        );
    }

    public StatisticsSubjectDataDto calculate (Subject subject, List<Activity> activities) {
        int totalActivitiesBySubject = activities.stream()
                .filter(activity -> activity.getSubject().equals(subject))
                .toList().size();

        int totalStudentsBySubject = subject.getStudents().size();
        
        return StatisticsSubjectDataMapper.toDto(
            subject.getId(), 
            (subject.getName()+" de "+ subject.getCourse().getYear().getName() + " " + subject.getCourse().getName()), 
            totalStudentsBySubject, 
            totalActivitiesBySubject
        );
    }

    public List<StatisticsActivityDataDto> calculate (List<Activity> activities) {
        
        List<StatisticsActivityDataDto> activitiesData = new ArrayList<>();

        for (Activity activity : activities) {
            int totalRealizations = activityCalculateTotalRealizationsService.execute(activity);
            int createdDaysAgo = (int) ChronoUnit.DAYS.between(
                activity.getCreatedAt().toLocalDate(), // pasa a LocalDate
                LocalDate.now()
            );
            activitiesData.add(StatisticsActivityDataMapper.toDto(activity.getName(), totalRealizations, createdDaysAgo));
        }
        
        return activitiesData;

    }
    
}
