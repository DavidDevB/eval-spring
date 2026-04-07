package app.controllers;

import app.dao.UserRepository;
import app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/login")
  public String login(Model model) {
    return "login";
  }

  @PostMapping("/login")
  public String login(
    @RequestParam String username,
    @RequestParam String password,
    Model model
  ) {
    User user = userRepository.findByUsername(username);
    if (user != null && user.getPassword().equals(password)) {
      model.addAttribute("user", user);
      return "index";
    }
    return "login";
  }

  @GetMapping("/logout")
  public String logout(Model model) {
    return "index";
  }
}
