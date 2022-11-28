package com.todoapp.backend.service;

import com.todoapp.backend.model.ToDo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ToDoService {

    private Integer idCounter = 1;
    //Java Collection that works as Repo
    private HashMap<Integer, ToDo> toDos = new HashMap<>();

    public ResponseEntity<?> createToDo(ToDo toDo) {

        if (toDo.getText().length() < 1 && toDo.getPriority().length() < 1 && toDo.getDueDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do has no body. Please fill!");
        else if (toDo.getPriority().length() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do has no priority. Please fill!");

        if (toDo.getText() != null) {
            if (toDo.getText().length() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do name can't be empty. Please fill!");
            else if (toDo.getText().length() > 120)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do name can't be more than 120 chars.");
        }

        ToDo newToDo = new ToDo(idCounter, toDo.getText(), toDo.getDueDate(), toDo.getPriority());
        toDos.put(idCounter, newToDo);
        return new ResponseEntity<>(toDos.get(idCounter++), HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteToDo(Integer id) {
        if (toDos.get(id) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "To-Do not found.");
        toDos.remove(id);
        return ResponseEntity.ok(toDos.values());
    }

    public ResponseEntity<?> deleteToDos() {
        if (toDos.size() == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No To-Do's found to delete.");
        toDos = new HashMap<>();
        idCounter = 1;
        return ResponseEntity.ok(toDos.values());
    }

    public ResponseEntity<?> updateTodo(Integer id, String remove, ToDo toDo) {
        if (toDo.getText() == null && toDo.getPriority() == null && remove.equals("false") && toDo.getDueDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Edit To-Do has no body. Please fill!");

        if (toDo.getText() != null) {
            if (toDo.getText().length() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do name can't be empty. Please fill!");
            else if (toDo.getText().length() > 120)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-Do name can't be more than 120 chars.");
        }

        ToDo toDoUpdated = toDos.get(id);

        if (toDoUpdated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "To-Do not found.");

        if (remove.equals("true") && toDoUpdated.getDueDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove an empty Due Date");

        if (toDo.getText() != null) toDoUpdated.setText(toDo.getText());
        if (toDo.getPriority() != null) toDoUpdated.setPriority(toDo.getPriority());
        if (toDo.getDueDate() != null) toDoUpdated.setDueDate(toDo.getDueDate());
        if (remove.equals("true")) toDoUpdated.setDueDate(null);

        toDos.put(id, toDoUpdated);
        return ResponseEntity.ok(toDos.get(id));
    }

    public ResponseEntity<?> setToDoDone(Integer id) {
        ToDo toDoUpdated = toDos.get(id);

        if (toDoUpdated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "To-Do not found.");
        if (toDoUpdated.getIsDone().equals("done")) return ResponseEntity.ok(toDos.get(id));

        toDoUpdated.setDoneDate(LocalDateTime.now());
        toDoUpdated.setIsDone("done");
        toDoUpdated.setBetweenDate(Duration.between(toDoUpdated.getCreationDate(), toDoUpdated.getDoneDate()));

        toDos.put(id, toDoUpdated);
        return ResponseEntity.ok(toDos.get(id));
    }

    public ResponseEntity<?>  setToDoUndone(Integer id) {
        ToDo toDoUpdated = toDos.get(id);

        if (toDoUpdated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "To-Do not found.");
        if (toDoUpdated.getIsDone().equals("undone")) return ResponseEntity.ok(toDos.get(id));

        toDoUpdated.setDoneDate(null);
        toDoUpdated.setIsDone("undone");
        toDoUpdated.setBetweenDate(null);

        toDos.put(id, toDoUpdated);
        return ResponseEntity.ok(toDos.get(id));
    }

    public ResponseEntity<?> getToDosStats() {
        HashMap<Integer, ToDo> toDos = getToDos();

        long all = 0L;
        long low = 0L;
        long medium = 0L;
        long high = 0L;
        int allCounter = 0;
        int lowCounter = 0;
        int mediumCounter = 0;
        int highCounter = 0;

        for (ToDo todo : toDos.values()) {
            if (todo.getBetweenDate() != null) {
                all += todo.getBetweenDate().toSeconds();
                allCounter++;
                if (todo.getPriority().equals("Low")) {
                    low += todo.getBetweenDate().toSeconds();
                    lowCounter++;
                }
                if (todo.getPriority().equals("Medium")) {
                    medium += todo.getBetweenDate().toSeconds();
                    mediumCounter++;
                }
                if (todo.getPriority().equals("High")) {
                    high += todo.getBetweenDate().toSeconds();
                    highCounter++;
                }
            }
        }

        if (allCounter == 0) allCounter++;
        if (lowCounter == 0) lowCounter++;
        if (mediumCounter == 0) mediumCounter++;
        if (highCounter == 0) highCounter ++;

        long allMinutes = (all/allCounter)/60;
        all = (all/allCounter) % 60;
        long lowMinutes = (low/lowCounter)/60;
        low = (low/lowCounter) % 60;
        long mediumMinutes = (medium/mediumCounter)/60;
        medium = (medium/mediumCounter) % 60;
        long highMinutes = (high/highCounter)/60;
        high = (high/highCounter) % 60;

        List<Long> stats = new ArrayList<>();
        stats.add(allMinutes);
        stats.add(all);
        stats.add(lowMinutes);
        stats.add(low);
        stats.add(mediumMinutes);
        stats.add(medium);
        stats.add(highMinutes);
        stats.add(high);

        return ResponseEntity.ok(stats);
    }

    public ResponseEntity<?>  getToDosSize(String name, String priority, String isDone) {
        //filter To Do's by name, priority and/or flag
        List<ToDo> toDosList = filter(name, priority, isDone);
        return ResponseEntity.ok(Math.max(toDosList.size(), 1));
    }

    public ResponseEntity<?> getToDosFiltered(String name, String priority, String isDone, String priorityOrder, String dueDateOrder, String page) {
        //filter To Do's by name, priority and/or flag
        List<ToDo> toDosList = filter(name, priority, isDone);

        //Setting to do priority to letters for order
        for (ToDo todo : toDosList) {
            switch (todo.getPriority()) {
                case "Low" -> todo.setPriority("a");
                case "Medium" -> todo.setPriority("b");
                case "High" -> todo.setPriority("c");
            }
        }

        //order
        List<ToDo> orderedToDosList = new ArrayList<>();

        if (!priorityOrder.equals("default") && !dueDateOrder.equals("default")) {  //order by priority and dueDate

            if (priorityOrder.equals("low")) {

                if (dueDateOrder.equals("normal"))
                    orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList()
                            .stream().sorted(Comparator.comparing(ToDo::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
                else if (dueDateOrder.equals("reversed"))
                    orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.reverseOrder()))).toList()
                            .stream().sorted(Comparator.comparing(ToDo::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))).toList();

            } else {

                if (dueDateOrder.equals("normal"))
                    orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList()
                            .stream().sorted(Comparator.comparing(ToDo::getPriority, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
                else
                    orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.reverseOrder()))).toList()
                            .stream().sorted(Comparator.comparing(ToDo::getPriority, Comparator.nullsLast(Comparator.reverseOrder()))).toList();

            }
        } else if (!priorityOrder.equals("default")) { //order by priority

            if (priorityOrder.equals("low")) //order priorityLow a->b->c
                orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getPriority)).toList();
            else                             //order priorityHigh c->b->a
                orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getPriority).reversed()).toList();

        } else if (!dueDateOrder.equals("default")) {  //order by dueDate

            if (dueDateOrder.equals("normal")) //order dueDate
                orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
            else                               //order dueDate reversed
                orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.reverseOrder()))).toList();

        } else {  //default order by id
            orderedToDosList = toDosList.stream().sorted(Comparator.comparing(ToDo::getId)).toList();
        }

        //Making the priority Low, Medium and High to show on front
        for (ToDo todo : orderedToDosList) {
            switch (todo.getPriority()) {
                case "a" -> todo.setPriority("Low");
                case "b" -> todo.setPriority("Medium");
                case "c" -> todo.setPriority("High");
            }
        }

        //pagination
        long pageForOffset = Long.parseLong(page);
        long offset = (pageForOffset - 1) * 10;
        orderedToDosList = orderedToDosList.stream().skip(offset).limit(10).toList();

        return ResponseEntity.ok(orderedToDosList);
    }

    //Methods for simplification.
    public HashMap<Integer, ToDo> getToDos() {
        return toDos;
    }

    public  List<ToDo> filter(String name, String priority, String isDone) {
        HashMap<Integer, ToDo> toDos = getToDos();
        List<ToDo> toDosList;

        //filter
        if (!name.equals("default") && !priority.equals("default") && !isDone.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getText().contains(name)).toList()
                    .stream().filter((p) -> p.getPriority().equals(priority)).toList()
                    .stream().filter((p) -> p.getIsDone().equals(isDone)).toList();
        }
        else if (!name.equals("default") && !priority.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getText().contains(name)).toList()
                    .stream().filter((p) -> p.getPriority().equals(priority)).toList();
        }
        else if (!name.equals("default") && !isDone.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getText().contains(name)).toList()
                    .stream().filter((p) -> p.getIsDone().equals(isDone)).toList();
        }
        else if (!priority.equals("default") && !isDone.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getPriority().equals(priority)).toList()
                    .stream().filter((p) -> p.getIsDone().equals(isDone)).toList();
        }
        else if (!name.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getText().contains(name)).toList();
        }
        else if (!priority.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getPriority().equals(priority)).toList();
        }
        else if (!isDone.equals("default")) {
            toDosList = toDos.values().stream().filter((p) -> p.getIsDone().equals(isDone)).toList();
        } else {
            toDosList = toDos.values().stream().toList();
        }

        return toDosList;
    }

    //Reset values to default for testing purposes.
    public void reset() {
        toDos = new HashMap<>();
        idCounter = 1;
    }
}
