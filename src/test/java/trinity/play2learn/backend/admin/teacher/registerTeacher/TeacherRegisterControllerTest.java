package trinity.play2learn.backend.admin.teacher.registerTeacher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import trinity.play2learn.backend.admin.teacher.controllers.TeacherRegisterController;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeacherRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITeacherRegisterService teacherRegisterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("✅ Registro exitoso")
    class SuccessfulRegistration {

        @Test
        @DisplayName("Debería registrar un docente correctamente con todos los datos anidados")
        void shouldRegisterTeacherSuccessfully() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Carlos")
                    .lastname("Gómez")
                    .email("carlos.gomez@example.com")
                    .dni("87654321")
                    .build();

            TeacherResponseDto responseDto = TeacherResponseDto.builder()
                    .id(1L)
                    .name("Carlos")
                    .lastname("Gómez")
                    .dni("87654321")
                    .user(UserResponseDto.builder()
                            .id(2L)
                            .email("carlos.gomez@example.com")
                            .build())
                    .build();

            Mockito.when(teacherRegisterService.cu5RegisterTeacher(Mockito.any()))
                    .thenReturn(responseDto);

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Carlos"))
                    .andExpect(jsonPath("$.data.lastname").value("Gómez"))
                    .andExpect(jsonPath("$.data.dni").value("87654321"))
                    .andExpect(jsonPath("$.data.user.email").value("carlos.gomez@example.com"))
                    .andExpect(jsonPath("$.message").value("Created succesfully"));
        }
    }

    @Nested
    @DisplayName("❌ Validaciones de email")
    class EmailValidationTests {

        @Test
        @DisplayName("No debe permitir registrar un docente con un email inválido")
        void shouldFailWhenEmailIsInvalid() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Ana")
                    .lastname("Díaz")
                    .email("ana.diaz") // inválido
                    .dni("11223344")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("email: Email is required."),
                                    is("email: Maximum length for email is 100 characters."),
                                    is("email: Email must be a valid email address.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir registrar un docente con email vacío")
        void shouldFailWhenEmailIsEmpty() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Ana")
                    .lastname("Díaz")
                    .email("") // vacío
                    .dni("11223344")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("email: Email is required."),
                                    is("email: Maximum length for email is 100 characters."),
                                    is("email: Email must be a valid email address.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir registrar un docente con email que supere 100 caracteres")
        void shouldFailWhenEmailExceedsMaxLength() throws Exception {
            // Arrange
            String emailMuyLargo = "a".repeat(90) + "@example.com"; // 90 + 10 = 100. Let's make it 101 to fail.
            emailMuyLargo = "a".repeat(91) + "@example.com"; // More than 100 characters
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Ana")
                    .lastname("Díaz")
                    .email(emailMuyLargo)
                    .dni("11223344")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("email: Email is required."),
                                    is("email: Maximum length for email is 100 characters."),
                                    is("email: Email must be a valid email address.")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("❌ Validaciones de DNI")
    class DniValidationTests {

        @Test
        @DisplayName("No debe permitir registrar un docente con puntos en el DNI")
        void shouldFailWhenDniContainsDots() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Roberto")
                    .lastname("Sánchez")
                    .email("roberto.sanchez@example.com")
                    .dni("99.888.777") // <-- inválido por puntos
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("dni: DNI is required."),
                                    is("dni: DNI must be exactly 8 digits.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir registrar un docente con DNI vacío")
        void shouldFailWhenDniIsEmpty() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Roberto")
                    .lastname("Sánchez")
                    .dni("") // vacío
                    .email("roberto.sanchez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("dni: DNI is required."),
                                    is("dni: DNI must be exactly 8 digits.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir registrar un docente con DNI que supere 8 dígitos")
        void shouldFailWhenDniExceedsMaxLength() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Roberto")
                    .lastname("Sánchez")
                    .dni("123456789") // 9 dígitos, inválido
                    .email("roberto.sanchez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("dni: DNI is required."),
                                    is("dni: DNI must be exactly 8 digits.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir registrar un docente con DNI que tenga menos de 8 dígitos")
        void shouldFailWhenDniIsLessThanEightDigits() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Roberto")
                    .lastname("Sánchez")
                    .dni("1234567") // 7 dígitos, inválido
                    .email("roberto.sanchez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("dni: DNI is required."),
                                    is("dni: DNI must be exactly 8 digits.")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("❌ Validaciones de nombre")
    class NameValidationTests {

        @Test
        @DisplayName("No debe permitir nombre con caracteres no alfabéticos")
        void shouldFailWhenNameContainsNonAlphabeticCharacters() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Laura123") // inválido
                    .lastname("Ramírez")
                    .dni("22334455")
                    .email("laura.ramirez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("name: Name is required."),
                                    is("name: Maximum length for name is 50 characters."),
                                    is("name: Name can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
                            )
                    )));
        }

        @Test
        @DisplayName("Debería permitir nombre con 50 caracteres (límite válido)")
        void shouldPassWhenNameHas50Characters() throws Exception {
            // Arrange
            String validName = "a".repeat(50);
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name(validName)
                    .lastname("Ramírez")
                    .dni("22334455")
                    .email("laura.ramirez@example.com")
                    .build();

            TeacherResponseDto responseDto = TeacherResponseDto.builder()
                    .id(1L)
                    .name(validName)
                    .lastname("Ramírez")
                    .dni("22334455")
                    .user(UserResponseDto.builder().id(2L).email("laura.ramirez@example.com").build())
                    .build();

            Mockito.when(teacherRegisterService.cu5RegisterTeacher(Mockito.any())).thenReturn(responseDto);

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value(validName));
        }

        @Test
        @DisplayName("No debe permitir nombre con 51 caracteres (límite excedido)")
        void shouldFailWhenNameHas51Characters() throws Exception {
            // Arrange
            String invalidName = "a".repeat(51);
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name(invalidName)
                    .lastname("Ramírez")
                    .dni("22334455")
                    .email("laura.ramirez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("name: Name is required."),
                                    is("name: Maximum length for name is 50 characters."),
                                    is("name: Name can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir nombre vacío")
        void shouldFailWhenNameIsEmpty() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("")
                    .lastname("Ramírez")
                    .dni("22334455")
                    .email("laura.ramirez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("name: Name is required."),
                                    is("name: Maximum length for name is 50 characters."),
                                    is("name: Name can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("❌ Validaciones de apellido")
    class LastnameValidationTests {

        @Test
        @DisplayName("No debe permitir apellido con caracteres no alfabéticos")
        void shouldFailWhenLastnameContainsNonAlphabeticCharacters() throws Exception {
        // Arrange
        TeacherRequestDto requestDto = TeacherRequestDto.builder()
            .name("Mario")
            .lastname("Fernández123") // inválido
            .dni("33445566")
            .email("mario.fernandez@example.com")
            .build();

        // Act + Assert
        mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", hasItem(
                    is("lastname: Lastname can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
            )));
    }

        @Test
        @DisplayName("Debería permitir apellido con 50 caracteres (límite válido)")
        void shouldPassWhenLastnameHas50Characters() throws Exception {
            // Arrange
            String validLastname = "b".repeat(50);
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Mario")
                    .lastname(validLastname)
                    .dni("33445566")
                    .email("mario.fernandez@example.com")
                    .build();

            TeacherResponseDto responseDto = TeacherResponseDto.builder()
                    .id(1L)
                    .name("Mario")
                    .lastname(validLastname)
                    .dni("33445566")
                    .user(UserResponseDto.builder().id(2L).email("mario.fernandez@example.com").build())
                    .build();

            Mockito.when(teacherRegisterService.cu5RegisterTeacher(Mockito.any())).thenReturn(responseDto);

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.lastname").value(validLastname));
        }

        @Test
        @DisplayName("No debe permitir apellido con 51 caracteres (límite excedido)")
        void shouldFailWhenLastnameHas51Characters() throws Exception {
            // Arrange
            String invalidLastname = "b".repeat(51);
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Mario")
                    .lastname(invalidLastname)
                    .dni("33445566")
                    .email("mario.fernandez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("lastname: Lastname is required."),
                                    is("lastname: Maximum length for Lastname is 50 characters."),
                                    is("lastname: Lastname can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
                            )
                    )));
        }

        @Test
        @DisplayName("No debe permitir apellido vacío")
        void shouldFailWhenLastnameIsEmpty() throws Exception {
            // Arrange
            TeacherRequestDto requestDto = TeacherRequestDto.builder()
                    .name("Mario")
                    .lastname("")
                    .dni("33445566")
                    .email("mario.fernandez@example.com")
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem(
                            anyOf(
                                    is("lastname: Lastname is required."),
                                    is("lastname: Maximum length for Lastname is 50 characters."),
                                    is("lastname: Lastname can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
                            )
                    )));
        }
    }
}
