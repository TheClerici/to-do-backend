package com.todoapp.backend.controller;

import com.todoapp.backend.model.ToDo;
import com.todoapp.backend.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ToDoController {

    @Autowired
    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping("/api/todos")
    public ResponseEntity<?> createToDo(@RequestBody ToDo toDo) {
        return toDoService.createToDo(toDo);
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<?> deleteToDo(@PathVariable Integer id) {
        return toDoService.deleteToDo(id);
    }

    @DeleteMapping("/api/todos")
    public ResponseEntity<?> deleteToDos() {
        return toDoService.deleteToDos();
    }

    @PutMapping("api/todos/{id}/{remove}")
    public HttpEntity<?> updateToDo(
            @PathVariable Integer id,
            @PathVariable String remove,
            @RequestBody ToDo toDo) {
        return toDoService.updateTodo(id, remove, toDo);
    }

    @PutMapping("/api/todos/{id}/done")
    public HttpEntity<?> toDoDone(@PathVariable Integer id) {
        return toDoService.setToDoDone(id);
    }

    @PutMapping("/api/todos/{id}/undone")
    public HttpEntity<?> toDoUndone(@PathVariable Integer id) {
        return toDoService.setToDoUndone(id);
    }

    @GetMapping("/api/todos/stats")
    public ResponseEntity<?> getToDosStats() {
        return toDoService.getToDosStats();
    }

    @RequestMapping(value = "/api/todos/size", params = {"name", "priority", "isDone"})
    public ResponseEntity<?> getToDosSize(
            @RequestParam(required = false, defaultValue = "default") String name,
            @RequestParam(required = false, defaultValue = "default") String priority,
            @RequestParam(required = false, defaultValue = "default") String isDone) {
        return toDoService.getToDosSize(name, priority, isDone);
    }

    @RequestMapping(value = "/api/todos", params = {"name", "priority", "isDone", "priorityOrder", "dueDateOrder", "page"})
    public ResponseEntity<?> getToDosFiltered(
            @RequestParam(required = false, defaultValue = "default") String name,
            @RequestParam(required = false, defaultValue = "default") String priority,
            @RequestParam(required = false, defaultValue = "default") String isDone,
            @RequestParam(required = false, defaultValue = "default") String priorityOrder,
            @RequestParam(required = false, defaultValue = "default") String dueDateOrder,
            @RequestParam(required = false, defaultValue = "1") String page) {
        return toDoService.getToDosFiltered(name, priority, isDone, priorityOrder, dueDateOrder, page);
    }
}