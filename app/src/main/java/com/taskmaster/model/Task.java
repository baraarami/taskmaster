package com.taskmaster.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "task_title")
  private final String title;

  @ColumnInfo(name = "task_body")
  private final String body;

  @ColumnInfo(name = "task_state")
  private final String status;

  public Task(String title, String body, String status) {
    this.title = title;
    this.body = body;
    this.status = status;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getStatus() {
    return status;
  }
}
