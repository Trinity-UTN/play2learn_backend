package trinity.play2learn.backend.student.registerStudent;

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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;


import trinity.play2learn.backend.admin.classes.dtos.ClassResponseDto;
import trinity.play2learn.backend.admin.student.controllers.StudentRegisterController;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.services.StudentRegisterService;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRegisterService studentRegisterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("✅ Registro exitoso")
    class SuccessfulRegistration {

        @Test
        @DisplayName("Debería registrar un estudiante correctamente con todos los datos anidados")
        void shouldRegisterStudentSuccessfully() throws Exception {
            // Arrange
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname("Pérez")
                    .email("juan.perez@example.com")
                    .dni("12345678")
                    .class_id(1L)
                    .build();

            StudentResponseDto responseDto = StudentResponseDto.builder()
                    .id(1L)
                    .name("Juan")
                    .lastname("Pérez")
                    .dni("12345678")
                    .user(UserResponseDto.builder()
                            .id(2L)
                            .email("juan.perez@example.com")
                            .build())
                    .classes(ClassResponseDto.builder()
                            .id(1L)
                            .name("3ro B")
                            .year(YearResponseDto.builder()
                                    .id(5L)
                                    .name("2025")
                                    .build())
                            .build())
                    .build();

            Mockito.when(studentRegisterService.cu4registerStudent(Mockito.any()))
                    .thenReturn(responseDto);

            // Act + Assert
            mockMvc.perform(post("/admin/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Juan"))
                    .andExpect(jsonPath("$.data.lastname").value("Pérez"))
                    .andExpect(jsonPath("$.data.dni").value("12345678"))
                    .andExpect(jsonPath("$.data.user.email").value("juan.perez@example.com"))
                    .andExpect(jsonPath("$.data.classes.name").value("3ro B"))
                    .andExpect(jsonPath("$.data.classes.year.name").value("2025"))
                    .andExpect(jsonPath("$.message").value("Created succesfully"));
        }
    }

    @Nested
    @DisplayName("❌ Validaciones de email")
    class EmailValidationTests {

        @Test
        @DisplayName("No debe permitir registrar un estudiante con un email inválido")
        void shouldFailWhenEmailIsInvalid() throws Exception {
            // Arrange
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname("Pérez")
                    .email("juan.perez") // inválido
                    .dni("12345678")
                    .class_id(1L)
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con email vacío")
        void shouldFailWhenEmailIsEmpty() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juana")
                    .lastname("Gómez")
                    .dni("87654321")
                    .email("")  // vacío
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con email que supere 100 caracteres")
        void shouldFailWhenEmailExceedsMaxLength() throws Exception {
            String emailMuyLargo = "a".repeat(101) + "@example.com"; // más de 100 caracteres
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juana")
                    .lastname("Gómez")
                    .dni("87654321")
                    .email(emailMuyLargo)
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con puntos en el DNI")
        void shouldFailWhenDniContainsDots() throws Exception {
            // Arrange
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname("Pérez")
                    .email("juan.perez@example.com")
                    .dni("12.345.678") // <-- inválido por puntos
                    .class_id(1L)
                    .build();

            // Act + Assert
            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con DNI vacío")
        void shouldFailWhenDniIsEmpty() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Pedro")
                    .lastname("García")
                    .dni("")  // vacío
                    .email("pedro.garcia@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con DNI que supere 8 dígitos")
        void shouldFailWhenDniExceedsMaxLength() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Pedro")
                    .lastname("García")
                    .dni("123456789")  // 9 dígitos, inválido
                    .email("pedro.garcia@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir registrar un estudiante con DNI que tenga menos de 8 dígitos")
        void shouldFailWhenDniIsLessThanEightDigits() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Lucas")
                    .lastname("Fernández")
                    .dni("1234567")  // 7 dígitos, inválido
                    .email("lucas.fernandez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("No debe permitir name con caracteres no alfabéticos")
        void shouldFailWhenNameContainsNonAlphabeticCharacters() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan123!") // inválido
                    .lastname("Perez")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("Debería permitir name con 49 caracteres (límite válido)")
        void shouldPassWhenNameHas49Characters() throws Exception {
            String validName = "a".repeat(49);

            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name(validName)
                    .lastname("Perez")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            StudentResponseDto responseDto = StudentResponseDto.builder()
                    .id(1L)
                    .name(validName)
                    .lastname("Perez")
                    .dni("12345678")
                    .user(UserResponseDto.builder().id(2L).email("juan.perez@example.com").build())
                    .classes(ClassResponseDto.builder().id(1L).name("3ro B").year(YearResponseDto.builder().id(5L).name("2025").build()).build())
                    .build();

            Mockito.when(studentRegisterService.cu4registerStudent(Mockito.any())).thenReturn(responseDto);

            mockMvc.perform(post("/admin/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value(validName));
        }

        @Test
        @DisplayName("No debe permitir name con 51 caracteres (límite excedido)")
        void shouldFailWhenNameHas51Characters() throws Exception {
            String invalidName = "a".repeat(51);

            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name(invalidName)
                    .lastname("Perez")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
        @DisplayName("No debe permitir name vacío")
        void shouldFailWhenNameIsEmpty() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("")
                    .lastname("Perez")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
    //-------------------------------------------------------
    @Nested
    @DisplayName("Lastame Validation Tests")
    class LastNameValidationTests {

        @Test
        @DisplayName("No debe permitir lastname con caracteres no alfabéticos")
        void shouldFailWhenNameContainsNonAlphabeticCharacters() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan") // inválido
                    .lastname("Perez123!")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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

        @Test
        @DisplayName("Debería permitir name con 49 caracteres (límite válido)")
        void shouldPassWhenNameHas49Characters() throws Exception {
            String validLastname = "a".repeat(49);

            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname(validLastname)
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            StudentResponseDto responseDto = StudentResponseDto.builder()
                    .id(1L)
                    .name("Juan")
                    .lastname(validLastname)
                    .dni("12345678")
                    .user(UserResponseDto.builder().id(2L).email("juan.perez@example.com").build())
                    .classes(ClassResponseDto.builder().id(1L).name("3ro B").year(YearResponseDto.builder().id(5L).name("2025").build()).build())
                    .build();

            Mockito.when(studentRegisterService.cu4registerStudent(Mockito.any())).thenReturn(responseDto);

            mockMvc.perform(post("/admin/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.lastname").value(validLastname));
        }

        @Test
        @DisplayName("No debe permitir name con 51 caracteres (límite excedido)")
        void shouldFailWhenNameHas51Characters() throws Exception {
            String invalidLastname = "a".repeat(51);

            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname(invalidLastname)
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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

        @Test
        @DisplayName("No debe permitir name vacío")
        void shouldFailWhenNameIsEmpty() throws Exception {
            StudentRequestDto requestDto = StudentRequestDto.builder()
                    .name("Juan")
                    .lastname("")
                    .dni("12345678")
                    .email("juan.perez@example.com")
                    .class_id(1L)
                    .build();

            mockMvc.perform(post("/admin/students")
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
    

