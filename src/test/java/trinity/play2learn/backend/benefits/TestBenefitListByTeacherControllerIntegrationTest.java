package trinity.play2learn.backend.benefits;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestBenefitListByTeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ITeacherRepository teacherRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IBenefitRepository benefitRepository;

    @MockBean
    private ImageUploadService imageUploadService;

    @Autowired
    private ISubjectRepository subjectRepository;

    @Autowired
    private ICourseRepository courseRepository;

    @Autowired
    private IYearRepository yearRepository;

    @Autowired
    private IJwtService jwtService;

    private static final String TEACHER_EMAIL = "juanpidealbera23@gmail.com";
    private static final String URL_ENDPOINT = "/benefits/teacher";
    private User user;
    
    @BeforeEach
    void setup() {
        benefitRepository.deleteAll();
        subjectRepository.deleteAll();
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        yearRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setEmail(TEACHER_EMAIL);
        user.setRole(Role.ROLE_TEACHER);
        user = userRepository.save(user);


    }

    @Test
    void testListBenefitsByTeacherSuccess() throws Exception {
        
         

        // Paso 1: Configurar los datos de prueba
        // Crear y guardar un profesor
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setName("Juan Pablo");
        teacher.setLastname("Dealbera"); // 👈 Añade el campo lastname
        teacher.setDni("43928343"); // 👈 Añade el campo dni
        teacherRepository.save(teacher);

        Year year = new Year(null, "2023/2024", null);
        year = yearRepository.save(year);

        Course course = new Course();
        course.setName("Primero");
        course.setYear(year);
        course = courseRepository.save(course);

        // Crear un subject
        Subject subject = new Subject();
        subject.setName("Matematica");
        subject.setTeacher(teacher); // Asocia el subject al profesor
        subject.setCourse(course);
        subject.setOptional(false);
        subject = subjectRepository.save(subject);

         // Crear y guardar dos beneficios asociados
        Benefit benefit1 = new Benefit();
        benefit1.setName("15 minutos mas de recreo");
        benefit1.setSubject(subject);
        benefit1.setCost(100L);
        benefit1.setPurchaseLimitPerStudent(1);
        benefit1.setCategory(BenefitCategory.EXTRAS);
        benefit1.setIcon(BenefitIcon.SKIP);
        benefit1.setColor(BenefitColor.RED);
        benefit1.setDescription("Un recreo extra de 15 minutos"); // 👈 Añade la descripción
        benefit1.setEndAt(LocalDateTime.now().plusDays(30)); // 👈 Añade la fecha de finalización
        benefitRepository.save(benefit1);

        Benefit benefit2 = new Benefit();
        benefit2.setName("Un punto mas en un parcial");
        benefit2.setSubject(subject);
        benefit2.setCost(100L);
        benefit2.setPurchaseLimitPerStudent(1);
        benefit2.setCategory(BenefitCategory.EXTRAS);
        benefit2.setIcon(BenefitIcon.SKIP);
        benefit2.setColor(BenefitColor.RED);
        benefit2.setDescription("Obtén un punto extra en un parcial"); // 👈 Añade la descripción
        benefit2.setEndAt(LocalDateTime.now().plusDays(30)); // 👈 Añade la fecha de finalización
        benefitRepository.save(benefit2);

        String token = jwtService.generateAccessToken(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL_ENDPOINT)
                        .header("Authorization", "Bearer " + token) // 👈 Agrega el header Authorization
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        // Paso 2: Validar el estado de la respuesta
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        // Paso 3: Validar el formato y contenido de la respuesta
        String content = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(content);

        // Validar el formato del BaseResponse
        assertNotNull(rootNode.get("data"));
        assertEquals("OK", rootNode.get("message").asText());
        assertTrue(rootNode.get("errors").isNull());
        assertNotNull(rootNode.get("timestamp"));
        
        JsonNode dataNode = rootNode.get("data");
        assertTrue(dataNode.isArray());
        assertEquals(2, dataNode.size());

        // Validar el contenido de cada beneficio en la lista
        JsonNode firstBenefit = dataNode.get(0);
        assertEquals("15 minutos mas de recreo", firstBenefit.get("name").asText());
        assertEquals(100L, firstBenefit.get("cost").asInt());
        assertEquals("EXTRAS", firstBenefit.get("category").asText());
        
        // Validar el campo anidado 'subjectDto'
        JsonNode subjectDto = firstBenefit.get("subjectDto");
        assertNotNull(subjectDto);
        assertEquals("Matematica", subjectDto.get("name").asText());

        JsonNode secondBenefit = dataNode.get(1);
        assertEquals("Un punto mas en un parcial", secondBenefit.get("name").asText());
        assertEquals(100, secondBenefit.get("cost").asInt());
        assertEquals("EXTRAS", secondBenefit.get("category").asText());
    }

    @Test
    void testListBenefitsByTeacherNoBenefitsFound() throws Exception {
        // Paso 1: Configurar los datos de prueba
        // Crear un profesor sin beneficios asociados
         Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setName("Juan Pablo");
        teacher.setLastname("Dealbera"); // 👈 Añade el campo lastname
        teacher.setDni("43928343"); // 👈 Añade el campo dni
        teacherRepository.save(teacher);

        String token = jwtService.generateAccessToken(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL_ENDPOINT)
                        .header("Authorization", "Bearer " + token) // 👈 Agrega el header Authorization
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Paso 2: Validar el estado de la respuesta
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        // Paso 3: Validar el contenido de la respuesta (lista vacía)
        String content = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(content);

        // Validar el formato y que el array 'data' está vacío
        assertEquals("OK", rootNode.get("message").asText());
        JsonNode dataNode = rootNode.get("data");
        assertTrue(dataNode.isArray());
        assertTrue(dataNode.isEmpty());
    }
}
