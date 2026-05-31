package trinity.play2learn.backend.configs.seed.services;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.configs.seed.dtos.SeedCountsDto;
import trinity.play2learn.backend.configs.seed.dtos.SeedCredentialsDto;

/**
 * Acumula credenciales y conteos durante la ejecución del seed.
 */
public class SeedDataCollector {

    private final List<SeedCredentialsDto> credentials = new ArrayList<>();

    private int reserves;

    private int years;

    private int courses;

    private int teachers;

    private int students;

    private int subjects;

    private int aspects;

    public void addCredential(SeedCredentialsDto credential) {
        credentials.add(credential);
    }

    public void incrementReserves() {
        reserves++;
    }

    public void incrementYears() {
        years++;
    }

    public void incrementCourses() {
        courses++;
    }

    public void incrementTeachers() {
        teachers++;
    }

    public void incrementStudents() {
        students++;
    }

    public void incrementSubjects() {
        subjects++;
    }

    public void incrementAspects() {
        aspects++;
    }

    public List<SeedCredentialsDto> getCredentials() {
        return List.copyOf(credentials);
    }

    public SeedCountsDto toCounts() {
        return SeedCountsDto.builder()
            .reserves(reserves)
            .years(years)
            .courses(courses)
            .teachers(teachers)
            .students(students)
            .subjects(subjects)
            .aspects(aspects)
            .build();
    }
}
