package trinity.play2learn.backend.admin.teacher.services.commons;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
class TeacherExistsByDniServiceTest {

    private static final String DNI = "12345678";
    private static final String ANOTHER_DNI = "87654321";
    private static final Long TEACHER_ID = 50L;

    @Mock
    private ITeacherRepository teacherRepository;

    private TeacherExistsByDniService teacherExistsByDniService;

    @BeforeEach
    void setUp() {
        teacherExistsByDniService = new TeacherExistsByDniService(teacherRepository);
    }

    @Nested
    @DisplayName("validate(dni)")
    class ValidateDni {

        @Test
        @DisplayName("Given dni unused When validate Then completes without exception")
        void whenDniUnused_doesNotThrow() {
            when(teacherRepository.existsByDni(DNI)).thenReturn(false);

            assertThatCode(() -> teacherExistsByDniService.validate(DNI)).doesNotThrowAnyException();
            verify(teacherRepository).existsByDni(DNI);
        }

        @Test
        @DisplayName("Given dni already used When validate Then throws ConflictException")
        void whenDniUsed_throwsConflict() {
            when(teacherRepository.existsByDni(DNI)).thenReturn(true);

            assertThatThrownBy(() -> teacherExistsByDniService.validate(DNI))
                .isInstanceOf(ConflictException.class);
        }
    }

    @Nested
    @DisplayName("validate(dni, id)")
    class ValidateDniExcludingId {

        @Test
        @DisplayName("Given dni unused for other teachers When validate Then completes without exception")
        void whenDniUnusedForOthers_doesNotThrow() {
            when(teacherRepository.existsByDniAndIdNot(DNI, TEACHER_ID)).thenReturn(false);

            assertThatCode(() -> teacherExistsByDniService.validate(DNI, TEACHER_ID)).doesNotThrowAnyException();
            verify(teacherRepository).existsByDniAndIdNot(DNI, TEACHER_ID);
        }

        @Test
        @DisplayName("Given dni used by another teacher When validate Then throws ConflictException")
        void whenDniUsedByAnother_throwsConflict() {
            when(teacherRepository.existsByDniAndIdNot(ANOTHER_DNI, TEACHER_ID)).thenReturn(true);

            assertThatThrownBy(() -> teacherExistsByDniService.validate(ANOTHER_DNI, TEACHER_ID))
                .isInstanceOf(ConflictException.class);
        }
    }
}

