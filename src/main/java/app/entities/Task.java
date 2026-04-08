package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private Date dueDate;
  private String description;
  private String status;
  private String type;

  public Task(
    String title,
    String description,
    String status,
    Date dueDate,
    String type
  ) {
    this.title = title;
    this.dueDate = dueDate;
    this.description = description;
    this.status = status;
    this.type = type;
  }

  public Task() {}

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return (
      "Task{" +
      "id=" +
      id +
      ", title='" +
      title +
      '\'' +
      ", dueDate=" +
      dueDate +
      ", description='" +
      description +
      '\'' +
      ", status='" +
      status +
      '\'' +
      '}'
    );
  }
}
