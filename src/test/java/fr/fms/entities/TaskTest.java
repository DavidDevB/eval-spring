package fr.fms.entities;

import app.entities.Task;
import org.junit.jupiter.api.Test;

class TaskTest {

  @Test
  void test_TaskHasAttributes() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("title");
    task.setDescription("description");
    task.setType("type");
    task.setStatus("status");
    task.setDueDate(new java.util.Date());

    assert task.getId() == 1L;
    assert task.getTitle().equals("title");
    assert task.getDescription().equals("description");
    assert task.getType().equals("type");
    assert task.getStatus().equals("status");
    assert task.getDueDate().equals(new java.util.Date());
  }

  @Test
  void test_TaskToString() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("title");
    task.setDescription("description");
    task.setType("type");
    task.setStatus("status");
    task.setDueDate(new java.util.Date());

    assert task
      .toString()
      .equals(
        "Task{id=1, title='title', dueDate=Thu Jan 01 00:00:00 CET 1970, description='description', status='status', type='type'}"
      );
  }
}
