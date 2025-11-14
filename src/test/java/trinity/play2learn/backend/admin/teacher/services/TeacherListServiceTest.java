package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;

@ExtendWith(MockitoExtension.class)
class TeacherListServiceTest {

    private static final Teacher ACTIVE_TEACHER = TeacherTestMother.teacher(11L, "12345678");
    private static final Teacher ANOTHER_TEACHER = TeacherTestMother.teacher(12L, "87654321");

    @Mock
    private ITeacherRepository teacherRepository;

    private TeacherListService teacherListService;

    @BeforeEach
    void setUp() {
        teacherListService = new TeacherListService(teacherRepository);
    }

    @Test
    @DisplayName("Given repository returns teachers When listing Then maps each teacher to response DTO")
    void whenTeachersExist_returnsMappedList() {
        when(teacherRepository.findAll()).thenReturn(List.of(ACTIVE_TEACHER, ANOTHER_TEACHER));

        List<TeacherResponseDto> responses = teacherListService.cu25ListTeachers();

        verify(teacherRepository).findAll();
        assertThat(responses)
            .extracting(TeacherResponseDto::getId, TeacherResponseDto::getDni)
            .containsExactlyInAnyOrder(
                org.assertj.core.groups.Tuple.tuple(ACTIVE_TEACHER.getId(), ACTIVE_TEACHER.getDni()),
                org.assertj.core.groups.Tuple.tuple(ANOTHER_TEACHER.getId(), ANOTHER_TEACHER.getDni())
            );
    }

    @Test
    @DisplayName("Given repository returns empty list When listing Then returns empty DTO list")
    void whenNoTeachersExist_returnsEmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        List<TeacherResponseDto> responses = teacherListService.cu25ListTeachers();

        verify(teacherRepository).findAll();
        assertThat(responses).isEmpty();
    }
}

