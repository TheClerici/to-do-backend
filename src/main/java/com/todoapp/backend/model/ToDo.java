package com.todoapp.backend.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ToDo {

    private Integer id;
    private String text; //max length 120 chars
    private LocalDate dueDate;
    private String priority;
    private String isDone;
    private LocalDateTime creationDate;
    private LocalDateTime doneDate;
    private Duration betweenDate;

    public ToDo(Integer id, String text, LocalDate dueDate, String priority) {
        this.id = id;
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isDone = "undone";
        this.creationDate = LocalDateTime.now();
        this.doneDate = null;
        this.betweenDate = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(LocalDateTime doneDate) {
        this.doneDate = doneDate;
    }

    public Duration getBetweenDate() {
        return betweenDate;
    }

    public void setBetweenDate(Duration betweenDate) {
        this.betweenDate = betweenDate;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", dueDate=" + dueDate +
                ", priority='" + priority + '\'' +
                ", isDone='" + isDone + '\'' +
                ", creationDate=" + creationDate +
                ", doneDate=" + doneDate +
                ", betweenDate=" + betweenDate +
                '}';
    }
}
