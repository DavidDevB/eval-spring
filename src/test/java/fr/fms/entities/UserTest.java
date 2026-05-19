package fr.fms.entities;

import app.entities.User;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void test_UserHasAttributes() {
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    user.setPassword("password");
    user.setRole("role");

    assert user.getId() == 1L;
    assert user.getUsername().equals("username");
    assert user.getPassword().equals("password");
    assert user.getRole().equals("role");
  }

  @Test
  void test_UserToString() {
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    user.setPassword("password");
    user.setRole("role");

    assert user
      .toString()
      .equals(
        "User{id=1, username='username', password='password', role='role'}"
      );
  }

  @Test
  void test_UserEquals() {
    User user1 = new User();
    user1.setId(1L);
    user1.setUsername("username");
    user1.setPassword("password");
    user1.setRole("role");

    User user2 = new User();
    user2.setId(1L);
    user2.setUsername("username");
    user2.setPassword("password");
    user2.setRole("role");

    assert user1.equals(user2);
  }
}
