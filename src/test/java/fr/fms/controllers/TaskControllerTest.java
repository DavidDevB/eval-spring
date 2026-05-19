package fr.fms.controllers;

import app.controllers.TaskController;
import app.dao.TaskRepository;
import app.entities.Task;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskController taskController;

    private Task todoTask;
    private Task inProgressTask;
    private Task doneTask;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        todoTask = new Task("Fix bug", "Fix the login bug", "todo", new Date(), "bug");
        todoTask.setId(1L);

        inProgressTask = new Task("Add feature", "Add dark mode", "in_progress", new Date(), "feature");
        inProgressTask.setId(2L);

        doneTask = new Task("Write docs", "Write API docs", "done", new Date(), "docs");
        doneTask.setId(3L);

        user = new User("admin", "password", "ADMIN");
    }

    // ----------------------------- GET / --------------------------------

    @Test
    void index_shouldReturnIndexViewWithCategorizedTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(todoTask, inProgressTask, doneTask));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("inProgressTasks"))
                .andExpect(model().attributeExists("completedTasks"));
    }

    @Test
    void index_withUserInSession_shouldAddUserToModel() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    void index_withoutUserInSession_shouldNotAddUserToModel() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("user"));
    }

    // ----------------------------- POST /add ----------------------------

    @Test
    void add_shouldSaveNewTaskAndRedirect() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", "New Task")
                        .param("description", "Some description")
                        .param("type", "bug"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ----------------------------- POST /inProgress ---------------------

    @Test
    void inProgress_shouldUpdateStatusToInProgressAndRedirect() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(todoTask));

        mockMvc.perform(post("/inProgress").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository).save(todoTask);
        assert "in_progress".equals(todoTask.getStatus());
    }

    @Test
    void inProgress_withUnknownId_shouldRedirectWithoutSaving() throws Exception {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/inProgress").param("id", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository, never()).save(any());
    }

    // ----------------------------- POST /complete -----------------------

    @Test
    void complete_shouldUpdateStatusToDoneAndRedirect() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(todoTask));

        mockMvc.perform(post("/complete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository).save(todoTask);
        assert "done".equals(todoTask.getStatus());
    }

    @Test
    void complete_withUnknownId_shouldRedirectWithoutSaving() throws Exception {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/complete").param("id", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository, never()).save(any());
    }

    // ----------------------------- POST /delete -------------------------

    @Test
    void delete_shouldDeleteTaskAndReturnIndexView() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(post("/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(taskRepository).deleteById(1L);
    }

    // ----------------------------- POST /search -------------------------

    @Test
    void search_shouldReturnIndexViewWithFilteredTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(todoTask, inProgressTask, doneTask));

        mockMvc.perform(post("/search").param("query", "Fix"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("inProgressTasks"))
                .andExpect(model().attributeExists("completedTasks"));
    }

    @Test
    void search_isCaseInsensitive() throws Exception {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(todoTask, inProgressTask, doneTask));

        // "fix" lowercase should match "Fix bug"
        mockMvc.perform(post("/search").param("query", "fix"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void search_withNoMatch_shouldReturnEmptyTasksList() throws Exception {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(todoTask, inProgressTask, doneTask));

        mockMvc.perform(post("/search").param("query", "xxxxxxxxxxx"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("tasks", List.of()));
    }

    // ----------------------------- GET /edit/{id} -----------------------

    @Test
    void editGet_shouldReturnEditViewWithTask() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(todoTask));

        mockMvc.perform(get("/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit/edit"))
                .andExpect(model().attribute("task", todoTask));
    }

    // ----------------------------- POST /edit/{id} ----------------------

    @Test
    void editPost_shouldUpdateTaskFieldsAndRedirect() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(todoTask));

        mockMvc.perform(post("/edit/1")
                        .param("title", "Updated Title")
                        .param("description", "Updated Description")
                        .param("type", "feature"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskRepository).save(todoTask);
        assert "Updated Title".equals(todoTask.getTitle());
        assert "Updated Description".equals(todoTask.getDescription());
        assert "feature".equals(todoTask.getType());
    }
}
