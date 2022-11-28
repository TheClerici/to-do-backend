package com.todoapp.backend;

import com.todoapp.backend.model.ToDo;
import com.todoapp.backend.service.ToDoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private ToDoService toDoService;

	@AfterEach
	void reset() {
		toDoService.reset();
	}

	@Test
	void successfullyCreateToDo() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		ResponseEntity<?> response = toDoService.createToDo(todo);
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		ResponseEntity<?> expected = new ResponseEntity<>(TODOS.get(1), HttpStatus.CREATED);
		assertEquals(expected, response);
	}

	@Test
	void createToDoWithNoBody() {
		try {
			ToDo todo = new ToDo(1, "", null, "");
			toDoService.createToDo(todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do has no body. Please fill!", RSE.getReason());
		}
	}

	@Test
	void createToDoWithBodyButNoPriority() {
		try {
			ToDo todo = new ToDo(1, "To do!", null, "");
			toDoService.createToDo(todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do has no priority. Please fill!", RSE.getReason());
		}
	}

	@Test
	void createToDoWithBodyButNoText() {
		try {
			ToDo todo = new ToDo(1, "", null, "Low");
			toDoService.createToDo(todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do name can't be empty. Please fill!", RSE.getReason());
		}
	}

	@Test
	void createToDoWithTextLongerThan120Characters() {
		try {
			String x = "This is a string that has more than a 120 characters, don't really try this at home and only" +
					"use it for tests purposes, it will really be amazing if I actually know how many characters I have," +
					"guess I'll have to test this now to know";
			ToDo todo = new ToDo(1, x, null, "Low");
			toDoService.createToDo(todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do name can't be more than 120 chars.", RSE.getReason());
		}
	}

	@Test
	void successfullyDeleteToDo() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		ToDo todoTwo = new ToDo(2, "TEST", null, "Low");
		toDoService.createToDo(todo);
		toDoService.createToDo(todoTwo);

		ResponseEntity<?> response = toDoService.deleteToDo(2);
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		ResponseEntity<?> expected = ResponseEntity.ok(TODOS.values());
		assertEquals(expected, response);
	}

	@Test
	void deleteToDoThatDoesNotExist() {
		try {
			toDoService.deleteToDo(10);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do not found.", RSE.getReason());
		}
	}

	@Test
	void successfullyDeleteAllToDos() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		ToDo todoTwo = new ToDo(2, "TEST", null, "Low");
		toDoService.createToDo(todo);
		toDoService.createToDo(todoTwo);

		ResponseEntity<?> response = toDoService.deleteToDos();
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		ResponseEntity<?> expected = ResponseEntity.ok(TODOS.values());
		assertEquals(expected, response);
	}

	@Test
	void deleteAllToDosWhenThereIsNone() {
		try {
			toDoService.deleteToDos();
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("No To-Do's found to delete.", RSE.getReason());
		}
	}

	@Test
	void successfullyUpdateToDo() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		toDoService.createToDo(todo);
		ToDo todoTwo = new ToDo(1, "TEST Updated", null, "Medium");
		ResponseEntity<?> response = toDoService.updateTodo(1, "false", todoTwo);
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		ResponseEntity<?> expected = ResponseEntity.ok(TODOS.get(1));
		assertEquals(expected, response);
	}

	@Test
	void updateToDoWithNoBody() {
		try {
			ToDo todo = new ToDo(1, "TEST", null, "Low");
			toDoService.createToDo(todo);
			ToDo todoTwo = new ToDo(null, null, null, null);
			toDoService.updateTodo(1, "false", todoTwo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("Edit To-Do has no body. Please fill!", RSE.getReason());
		}
	}

	@Test
	void updateToDoWithEmptyText() {
		try {
			ToDo todo = new ToDo(1, "To do!", null, "Low");
			toDoService.createToDo(todo);
			ToDo todoTwo = new ToDo(null, "", null, "Medium");
			toDoService.updateTodo(1, "false", todoTwo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do name can't be empty. Please fill!", RSE.getReason());
		}
	}

	@Test
	void updateToDoWithTextLongerThan120Characters() {
		try {
			String x = "This is a string that has more than a 120 characters, don't really try this at home and only" +
					"use it for tests purposes, it will really be amazing if I actually know how many characters I have," +
					"guess I'll have to test this now to know";
			ToDo todo = new ToDo(1, "To do!", null, "Low");
			toDoService.createToDo(todo);
			ToDo todoTwo = new ToDo(null, x, null, "Medium");
			toDoService.updateTodo(1, "false", todoTwo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do name can't be more than 120 chars.", RSE.getReason());
		}
	}

	@Test
	void updateToDoThatDoesNotExist() {
		try {
			ToDo todo = new ToDo(1,"To do!", null, "Low");
			toDoService.updateTodo(1, "false", todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do not found.", RSE.getReason());
		}
	}

	@Test
	void updateToDoByRemovingDueDateThatIsAlreadyNull() {
		try {
			ToDo todo = new ToDo(1,"To do!", null, "Low");
			toDoService.createToDo(todo);
			toDoService.updateTodo(1, "true", todo);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("Can't remove an empty Due Date", RSE.getReason());
		}
	}

	@Test
	void successfullySetToDoToDone() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		toDoService.createToDo(todo);

		toDoService.setToDoDone(1);
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		String response = TODOS.get(1).getIsDone();
		String expected = "done";
		assertEquals(expected, response);
	}

	@Test
	void setToDoToDoneThatDoesNotExist() {
		try {
			toDoService.setToDoDone(10);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do not found.", RSE.getReason());
		}
	}

	@Test
	void successfullySetToDoToUndone() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		toDoService.createToDo(todo);
		toDoService.setToDoDone(1);
		toDoService.setToDoUndone(1);
		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		String response = TODOS.get(1).getIsDone();
		String expected = "undone";
		assertEquals(expected, response);
	}

	@Test
	void setToDoToUndoneThatDoesNotExist() {
		try {
			toDoService.setToDoUndone(10);
			fail();
		} catch (ResponseStatusException RSE) {
			assertEquals("To-Do not found.", RSE.getReason());
		}
	}

	@Test
	void testEmptyStats() {
		ResponseEntity<?> response = toDoService.getToDosStats();
		List<Long> stats = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
		ResponseEntity<?> expected = ResponseEntity.ok(stats);
		assertEquals(expected, response);
	}

	@Test
	void testStats() {
		ToDo todoLow = new ToDo(1, "TEST", null, "Low");
		toDoService.createToDo(todoLow);
		ToDo todoMedium = new ToDo(2, "TEST", null, "Medium");
		toDoService.createToDo(todoMedium);
		ToDo todoHigh = new ToDo(3, "TEST", null, "High");
		toDoService.createToDo(todoHigh);

		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		TODOS.get(1).setBetweenDate(Duration.parse("PT1M30S"));
		TODOS.get(2).setBetweenDate(Duration.parse("PT3M"));
		TODOS.get(3).setBetweenDate(Duration.parse("PT4M30S"));

		ResponseEntity<?> response = getToDosStatsForTesting(TODOS);
		List<Long> stats = Arrays.asList(3L, 0L, 1L, 30L, 3L, 0L, 4L, 30L);
		ResponseEntity<?> expected = ResponseEntity.ok(stats);
		assertEquals(expected, response);
	}

	@Test
	void testToDosSizeEmpty() {
		ResponseEntity<?> response = toDoService.getToDosSize("default", "default", "default");
		//Can't be zero because it is used for pagination, if empty = 1.
		ResponseEntity<?> expected = ResponseEntity.ok(1);
		assertEquals(expected, response);
	}

	@Test
	void testToDosSize3() {
		ToDo todo = new ToDo(1, "TEST", null, "Low");
		toDoService.createToDo(todo);
		toDoService.createToDo(todo);
		toDoService.createToDo(todo);
		ResponseEntity<?> response = toDoService.getToDosSize("default", "default", "default");
		ResponseEntity<?> expected = ResponseEntity.ok(3);
		assertEquals(expected, response);
	}

	@Test
	void testToDosSizeWithFilterPriorityLow() {
		ToDo todoLow = new ToDo(1, "TEST", null, "Low");
		ToDo todoLow2 = new ToDo(1, "TEST", null, "Low");
		ToDo todoMedium = new ToDo(3, "TEST", null, "Medium");
		ToDo todoHigh = new ToDo(4, "TEST", null, "High");
		toDoService.createToDo(todoLow);
		toDoService.createToDo(todoLow2);
		toDoService.createToDo(todoMedium);
		toDoService.createToDo(todoHigh);
		ResponseEntity<?> response = toDoService.getToDosSize("default", "Low", "default");
		ResponseEntity<?> expected = ResponseEntity.ok(2);
		assertEquals(expected, response);
	}

	@Test
	void TestGetToDosFilteredEmpty() {
		ResponseEntity<?> response = toDoService.getToDosFiltered("default", "default","default","default","default", "1");
		List<ToDo> TODOS = new ArrayList<>();
		ResponseEntity<?> expected = ResponseEntity.ok(TODOS);
		assertEquals(expected, response);
	}

	@Test
	void TestGetToDosFilteredWithDummies() {
		ToDo todo1 = new ToDo(1, "TEST 1", LocalDate.parse("2023-01-02"), "Low");
		toDoService.createToDo(todo1);
		ToDo todo2 = new ToDo(2, "TEST 2", LocalDate.parse("2023-01-01"), "Low");
		toDoService.createToDo(todo2);
		ToDo todo3 = new ToDo(3, "TEST 3", LocalDate.parse("2023-02-10"), "Medium");
		toDoService.createToDo(todo3);
		ToDo todo4 = new ToDo(4, "TEST 4", LocalDate.parse("2023-01-03"), "Medium");
		toDoService.createToDo(todo4);
		ToDo todo5 = new ToDo(5, "TEST 5", LocalDate.parse("2023-03-20"), "High");
		toDoService.createToDo(todo5);
		ToDo todo6 = new ToDo(6, "TEST 6", LocalDate.parse("2023-03-21"), "High");
		toDoService.createToDo(todo6);

		toDoService.setToDoDone(2);
		toDoService.setToDoDone(4);
		toDoService.setToDoDone(6);

		HashMap<Integer, ToDo> TODOS = toDoService.getToDos();
		List<ToDo> LIST = new ArrayList<>();
		LIST.add(TODOS.get(6));
		LIST.add(TODOS.get(4));
		LIST.add(TODOS.get(2));

		//Testing to get the ones that include TEST on name, are Done and from priority order High to Low.
		ResponseEntity<?> response = toDoService.getToDosFiltered
				("TEST", "default", "done", "highToLow", "default", "1");
		ResponseEntity<?> expected = ResponseEntity.ok(LIST);
		assertEquals(expected, response);
	}

	//Stats for testing as I need to use certain parameters to test.
	public ResponseEntity<?> getToDosStatsForTesting(HashMap<Integer, ToDo> TODOS) {
		long all = 0L;
		long low = 0L;
		long medium = 0L;
		long high = 0L;
		int allCounter = 0;
		int lowCounter = 0;
		int mediumCounter = 0;
		int highCounter = 0;

		for (ToDo todo : TODOS.values()) {
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
}