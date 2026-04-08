package app.controllers;

import app.dao.TaskRepository;
import app.entities.Task;
import app.entities.User;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaskController {

  @Autowired
  private TaskRepository taskRepository;

  @GetMapping("/")
  public String index(Model model, HttpSession session) {
    List<Task> all = taskRepository.findAll();
    model.addAttribute(
      "tasks",
      all
        .stream()
        .filter(t -> !"done".equals(t.getStatus()))
        .filter(t -> !"in_progress".equals(t.getStatus()))
        .toList()
    );
    model.addAttribute(
      "inProgressTasks",
      all
        .stream()
        .filter(t -> "in_progress".equals(t.getStatus()))
        .toList()
    );
    model.addAttribute(
      "completedTasks",
      all
        .stream()
        .filter(t -> "done".equals(t.getStatus()))
        .toList()
    );
    User user = (User) session.getAttribute("user");
    if (user != null) {
      model.addAttribute("user", user);
    }
    return "index";
  }

  @PostMapping("/add")
  public String add(
    @RequestParam String title,
    @RequestParam String description,
    @RequestParam String type
  ) {
    taskRepository.save(new Task(title, description, "todo", new Date(), type));
    return "redirect:/";
  }

  @PostMapping("/inProgress")
  public String inProgress(@RequestParam Long id) {
    taskRepository
      .findById(id)
      .ifPresent(t -> {
        t.setStatus("in_progress");
        taskRepository.save(t);
      });
    return "redirect:/";
  }

  @PostMapping("/complete")
  public String complete(@RequestParam Long id) {
    taskRepository
      .findById(id)
      .ifPresent(t -> {
        t.setStatus("done");
        taskRepository.save(t);
      });
    return "redirect:/";
  }

  @PostMapping("/delete")
  public String delete(
    @RequestParam Long id,
    Model model,
    HttpSession session
  ) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    taskRepository.deleteById(id);
    model.addAttribute("tasks", taskRepository.findAll());
    return "index";
  }

  @PostMapping("/search")
  public String search(
    @RequestParam String query,
    Model model,
    HttpSession session
  ) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    List<Task> all = taskRepository.findAll();
    model.addAttribute(
      "tasks",
      all
        .stream()
        .filter(t -> t.getTitle().toLowerCase().contains(query.toLowerCase()))
        .toList()
    );
    return "index";
  }

  @GetMapping("/edit/{id}")
  public String modify(
    @PathVariable Long id,
    Model model,
    HttpSession session
  ) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    model.addAttribute("task", taskRepository.findById(id).get());
    return "edit/edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(
    @PathVariable Long id,
    @RequestParam String title,
    @RequestParam String description,
    @RequestParam String type
  ) {
    Task task = taskRepository.findById(id).get();
    task.setTitle(title);
    task.setDescription(description);
    task.setType(type);
    taskRepository.save(task);
    return "redirect:/";
  }
}
