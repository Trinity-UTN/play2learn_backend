package trinity.play2learn.backend.admin.teacher.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.teacher.TeacherTestMother;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;

@ExtendWith(MockitoExtension.class)
class TeacherGetServiceTest {

    private static final long TEACHER_ID = 5L;
    private static final String TEACHER_DNI = "12345678";

    @Mock
    private ITeacherGetByIdService teacherGetByIdService;

    private TeacherGetService teacherGetService;

    @BeforeEach
    void setUp() {
        teacherGetService = new TeacherGetService(teacherGetByIdService);
    }

    @Test
    @DisplayName("Given existing teacher When retrieving by id Then returns mapped response")
    void whenTeacherExists_returnsDto() {
        Teacher teacher = TeacherTestMother.teacher(TEACHER_ID, TEACHER_DNI);
        when(teacherGetByIdService.findById(TEACHER_ID)).thenReturn(teacher);

        TeacherResponseDto response = teacherGetService.cu28GetTeacherById(TEACHER_ID);

        verify(teacherGetByIdService).findById(TEACHER_ID);
        assertThat(response)
            .extracting(TeacherResponseDto::getId, TeacherResponseDto::getDni, TeacherResponseDto::isActive)
            .containsExactly(TEACHER_ID, TEACHER_DNI, true);
    }
}

