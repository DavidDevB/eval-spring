package app.controllers;

import app.dao.TaskRepository;
import app.entities.Task;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaskController {

  @Autowired
  private TaskRepository taskRepository;

  @GetMapping("/")
  public String index(Model model) {
    List<Task> all = taskRepository.findAll();
    model.addAttribute(
      "tasks",
      all
        .stream()
        .filter(t -> !"done".equals(t.getStatus()))
        .toList()
    );
    model.addAttribute(
      "inProgress",
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
    return "index";
  }

  @PostMapping("/add")
  public String add(
    @RequestParam String title,
    @RequestParam String description
  ) {
    taskRepository.save(new Task(title, description, "inProgress", new Date()));
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
}
