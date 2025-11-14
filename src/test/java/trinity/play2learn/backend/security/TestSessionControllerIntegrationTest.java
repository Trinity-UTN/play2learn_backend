package trinity.play2learn.backend.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;

@Disabled("Legacy integration test desactivado temporalmente para estabilizar la suite")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestSessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IJwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @MockBean
    private ImageUploadService imageUploadService;

    private User teacherUser;
    private User adminUser;
    private User studentUser;
    private User devUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        teacherUser = new User();
        teacherUser.setEmail("teacher@test.com");
        teacherUser.setRole(Role.ROLE_TEACHER);
        teacherUser = userRepository.save(teacherUser);

        adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser = userRepository.save(adminUser);

        studentUser = new User();
        studentUser.setEmail("student@test.com");
        studentUser.setRole(Role.ROLE_STUDENT);
        studentUser = userRepository.save(studentUser);

        devUser = new User();
        devUser.setEmail("dev@test.com");
        devUser.setRole(Role.ROLE_DEV);
        devUser = userRepository.save(devUser);
    }

    // -------------------------------
    // Tests de @SessionRequired para cada rol
    // -------------------------------

    @Test
    void teacherEndpoint_validToken_shouldReturn200() throws Exception {
        String token = jwtService.generateAccessToken(teacherUser);

        mockMvc.perform(get("/api/test/session/teacher")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Todo correcto"));

    }

    @Test
    void teacherEndpoint_invalidToken_shouldReturnUnauthorized() throws Exception {
        String token = jwtService.generateAccessToken(adminUser); // otro rol

        mockMvc.perform(get("/api/test/session/teacher")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_validToken_shouldReturn200() throws Exception {
        String token = jwtService.generateAccessToken(adminUser);

        mockMvc.perform(get("/api/test/session/admin")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Todo correcto"));
    }

    @Test
    void adminEndpoint_invalidToken_shouldReturnUnauthorized() throws Exception {
        String token = jwtService.generateAccessToken(teacherUser);

        mockMvc.perform(get("/api/test/session/admin")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentEndpoint_validToken_shouldReturn200() throws Exception {
        String token = jwtService.generateAccessToken(studentUser);

        mockMvc.perform(get("/api/test/session/student")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Todo correcto"));
    }

    @Test
    void studentEndpoint_invalidToken_shouldReturnUnauthorized() throws Exception {
        String token = jwtService.generateAccessToken(adminUser);

        mockMvc.perform(get("/api/test/session/student")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devEndpoint_validToken_shouldReturn200() throws Exception {
        String token = jwtService.generateAccessToken(devUser);

        mockMvc.perform(get("/api/test/session/dev")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Todo correcto"));
    }

    @Test
    void devEndpoint_invalidToken_shouldReturnUnauthorized() throws Exception {
        String token = jwtService.generateAccessToken(studentUser);

        mockMvc.perform(get("/api/test/session/dev")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------
    // Tests de @SessionUser
    // -------------------------------

    @Test
    void userEndpoint_validToken_shouldInjectUser() throws Exception {
        String token = jwtService.generateAccessToken(adminUser);

        mockMvc.perform(get("/api/test/session/user")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("admin@test.com"))
                .andExpect(jsonPath("$.data.role").value("ROLE_ADMIN"));
    }

    @Test
    void userEndpoint_invalidToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test/session/user")) // sin token
                .andExpect(status().isUnauthorized());
    }
}
