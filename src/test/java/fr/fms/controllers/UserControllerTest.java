package fr.fms.controllers;

import app.controllers.UserController;
import app.dao.UserRepository;
import app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User("admin", "password123", "ADMIN");
    }

    // ----------------------------- GET /login ---------------------------

    @Test
    void loginGet_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }

    // ----------------------------- POST /login --------------------------

    @Test
    void loginPost_withValidCredentials_shouldRedirectToIndex() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginPost_withValidCredentials_shouldStoreUserInSession() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(user);
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(session)
                        .param("username", "admin")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection());

        assert user.equals(session.getAttribute("user"));
    }

    @Test
    void loginPost_withWrongPassword_shouldReturnLoginView() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }

    @Test
    void loginPost_withWrongPassword_shouldNotStoreUserInSession() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(user);
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(session)
                        .param("username", "admin")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk());

        assert session.getAttribute("user") == null;
    }

    @Test
    void loginPost_withUnknownUsername_shouldReturnLoginView() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        mockMvc.perform(post("/login")
                        .param("username", "unknown")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }

    @Test
    void loginPost_withUnknownUsername_shouldNotStoreUserInSession() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(session)
                        .param("username", "unknown")
                        .param("password", "password123"))
                .andExpect(status().isOk());

        assert session.getAttribute("user") == null;
    }

    // ----------------------------- GET /logout --------------------------

    @Test
    void logout_shouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}