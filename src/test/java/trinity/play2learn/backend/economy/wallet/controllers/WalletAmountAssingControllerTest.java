package trinity.play2learn.backend.economy.wallet.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletAmountAssingController.class)
@AutoConfigureMockMvc(addFilters = false)
class WalletAmountAssingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionGenerateService generateTransactionService;

    @MockBean
    private IStudentGetByIdService studentGetByIdService;

    @MockBean
    private ISubjectGetByIdService subjectGetByIdService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("POST /wallet/test/{id}")
    class AsignAmount {

        @Test
        @DisplayName("Given valid student ID When assigning amount Then returns 204 No Content")
        void whenValidStudentId_returnsNoContent() throws Exception {
            // Given - Estudiante válido y materia existente
            Long studentId = EconomyTestMother.DEFAULT_STUDENT_ID;
            Long subjectId = 3L;
            
            Student student = EconomyTestMother.student(studentId, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Subject subject = EconomyTestMother.defaultSubject();

            Mockito.when(studentGetByIdService.findById(studentId))
                .thenReturn(student);
            Mockito.when(subjectGetByIdService.findById(subjectId))
                .thenReturn(subject);
            Mockito.when(generateTransactionService.generate(
                Mockito.any(),
                Mockito.anyDouble(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            )).thenReturn(EconomyTestMother.defaultTransaction());

            // When & Then - POST /wallet/test/{id} debe retornar 204
            mockMvc.perform(post("/wallet/test/{id}", studentId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicios fueron llamados correctamente
            Mockito.verify(studentGetByIdService).findById(studentId);
            Mockito.verify(subjectGetByIdService).findById(subjectId);
            Mockito.verify(generateTransactionService).generate(
                Mockito.any(),
                Mockito.eq(25000.0),
                Mockito.eq("Recompensa a estudiante"),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            );
        }

        @Test
        @DisplayName("Given non-existing student ID When assigning amount Then returns 404 Not Found")
        void whenNonExistingStudentId_returnsNotFound() throws Exception {
            // Given - Estudiante no existe
            Long studentId = EconomyTestMother.DEFAULT_STUDENT_ID;

            Mockito.when(studentGetByIdService.findById(studentId))
                .thenThrow(new NotFoundException("Estudiante no encontrado"));

            // When & Then - POST /wallet/test/{id} debe retornar 404
            mockMvc.perform(post("/wallet/test/{id}", studentId))
                .andExpect(status().isNotFound());

            // Verify - Servicio de estudiante fue llamado, pero no los demás
            Mockito.verify(studentGetByIdService).findById(studentId);
            Mockito.verifyNoInteractions(subjectGetByIdService);
            Mockito.verifyNoInteractions(generateTransactionService);
        }

        @Test
        @DisplayName("Given valid student ID but non-existing subject When assigning amount Then returns 404 Not Found")
        void whenValidStudentIdButNonExistingSubject_returnsNotFound() throws Exception {
            // Given - Estudiante válido pero materia no existe
            Long studentId = EconomyTestMother.DEFAULT_STUDENT_ID;
            Long subjectId = 3L;
            
            Student student = EconomyTestMother.student(studentId, EconomyTestMother.DEFAULT_STUDENT_EMAIL);

            Mockito.when(studentGetByIdService.findById(studentId))
                .thenReturn(student);
            Mockito.when(subjectGetByIdService.findById(subjectId))
                .thenThrow(new NotFoundException("Materia no encontrada"));

            // When & Then - POST /wallet/test/{id} debe retornar 404
            mockMvc.perform(post("/wallet/test/{id}", studentId))
                .andExpect(status().isNotFound());

            // Verify - Servicios fueron llamados pero transacción no se genera
            Mockito.verify(studentGetByIdService).findById(studentId);
            Mockito.verify(subjectGetByIdService).findById(subjectId);
            Mockito.verifyNoInteractions(generateTransactionService);
        }
    }
}

