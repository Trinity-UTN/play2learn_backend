package trinity.play2learn.backend.admin.student.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentExistByDNIServiceTest {

    private static final String DNI = "12345678";

    @Mock
    private IStudentRepository studentRepository;

    private StudentExistByDNIService studentExistByDNIService;

    @BeforeEach
    void setUp() {
        studentExistByDNIService = new StudentExistByDNIService(studentRepository);
    }

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        @DisplayName("Given existing DNI When validating Then returns true")
        void whenDniExists_returnsTrue() {
            when(studentRepository.existsByDni(DNI)).thenReturn(true);

            boolean result = studentExistByDNIService.validate(DNI);

            assertAll(
                () -> verify(studentRepository).existsByDni(DNI),
                () -> assertThat(result).isTrue()
            );
        }

        @Test
        @DisplayName("Given missing DNI When validating Then returns false")
        void whenDniNotExists_returnsFalse() {
            when(studentRepository.existsByDni(DNI)).thenReturn(false);

            boolean result = studentExistByDNIService.validate(DNI);

            assertAll(
                () -> verify(studentRepository).existsByDni(DNI),
                () -> assertThat(result).isFalse()
            );
        }
    }
}

