package service.http.handlers;

import com.google.gson.Gson;
import model.enums.Status;
import model.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.http.HttpTaskServer;
import service.http.adapters.TasksListTypeToken;
import service.interfaces.TaskManager;
import service.managers.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest {
    TaskManager inMemoryTaskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = httpTaskServer.getGson();

    TasksHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllSubTasks();
        inMemoryTaskManager.deleteAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testPostTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasks();

        assertEquals(201, response.statusCode(), "Неверный код ответа.");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void testPostUpdateTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createTask(new Task("Задача 2", "Описание задачи 2",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(60)));

        Task updateTask = new Task("Обновлённая задача 2", "Обновлённое описание задачи 2",
                Status.DONE, LocalDateTime.of(2024, 6, 30, 10, 0),
                Duration.ofMinutes(60));

        String taskJson = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertEquals(201, response.statusCode());
        assertEquals("Обновлённая задача 2", inMemoryTaskManager.getByIdTasks(1).getName(),
                "Название задачи не обновилось.");
        assertEquals("Обновлённое описание задачи 2", inMemoryTaskManager.getByIdTasks(1).getDescription(),
                "Описание задачи не обновилось.");
        assertEquals(Status.DONE, inMemoryTaskManager.getByIdTasks(1).getStatus(), "Статус задачи не обновился.");
        assertEquals(LocalDateTime.of(2024, 6, 30, 10, 0),
                inMemoryTaskManager.getByIdTasks(1).getStartTime(), "Время начала задачи не обновилось.");
        assertEquals(Duration.ofMinutes(60), inMemoryTaskManager.getByIdTasks(1).getDuration(),
                "Продолжение задачи не обновилось.");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createTask(new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(60)));
        inMemoryTaskManager.createTask(new Task("Задача 2", "Описание задачи 2",
                Status.NEW, LocalDateTime.of(2024, 6, 25, 10, 0),
                Duration.ofMinutes(60)));
        inMemoryTaskManager.createTask(new Task("Задача 3", "Описание задачи 3",
                Status.NEW, LocalDateTime.of(2024, 7, 25, 10, 0),
                Duration.ofMinutes(60)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasks();

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TasksListTypeToken().getType());

        assertEquals(tasksFromManager, tasksFromResponse, "Получены не все задачи.");
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskId() throws IOException, InterruptedException {
        inMemoryTaskManager.createTask(new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(60)));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        Task taskResponse = gson.fromJson(response.body(), Task.class);

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasks();

        assertEquals(taskResponse, inMemoryTaskManager.getByIdTasks(1));
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(tasksFromManager, "Задача не возвращается");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createTask(new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(60)));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasks();

        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertTrue(tasksFromManager.isEmpty(), "Задача не удалились.");
    }

    @Test
    public void timeCrossingTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(90));
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание задачи 2",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 11, 0),
                Duration.ofMinutes(90));
        task2.setId(2);
        Task task3 = new Task("Задача 3", "Описание задачи 3",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 15, 0),
                Duration.ofMinutes(90));
        task3.setId(3);
        Task task4 = new Task("Задача 4", "Описание задачи 4",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 16, 0),
                Duration.ofMinutes(90));
        task4.setId(4);
        Task task5 = new Task("Задача 5", "Описание задачи 5",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 20, 0),
                Duration.ofMinutes(90));
        task5.setId(5);


        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);
        String task3Json = gson.toJson(task3);
        String task4Json = gson.toJson(task4);
        String task5Json = gson.toJson(task5);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(task3Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(task4Json)).build();
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(task5Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasks();

        assertEquals(201, response1.statusCode(), "Неверный код ответа.");
        assertEquals(406, response2.statusCode(), "Найдено пересечение.");
        assertEquals(201, response3.statusCode(), "Неверный код ответа.");
        assertEquals(406, response4.statusCode(), "Найдено пересечение.");
        assertEquals(201, response5.statusCode(), "Неверный код ответа.");
        assertEquals("Задача 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Задача 3", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Задача 5", tasksFromManager.get(2).getName(), "Некорректное имя задачи");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        assertFalse(tasksFromManager.contains(task2), "Задача с пересечением добавилась.");
        assertFalse(tasksFromManager.contains(task4), "Задача с пересечением добавилась.");
        assertTrue(tasksFromManager.contains(task1));
        assertTrue(tasksFromManager.contains(task3));
        assertTrue(tasksFromManager.contains(task5));
    }
}



