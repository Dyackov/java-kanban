package service.http.handlers;

import com.google.gson.Gson;
import model.enums.Status;
import model.tasks.Epic;
import model.tasks.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.http.HttpTaskServer;
import service.http.adapters.SubTasksListTypeToken;
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

class SubtasksHandlerTest {
    TaskManager inMemoryTaskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = httpTaskServer.getGson();

    SubtasksHandlerTest() throws IOException {
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
    public void testPostSubTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));

        SubTask subTask = new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(30));

        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<SubTask> subTasksFromManager = inMemoryTaskManager.getAllSubTask();

        assertEquals(201, response.statusCode(), "Неверный код ответа.");
        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Под задача 1", subTasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }


    @Test
    public void testPostUpdateSubTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createSubtask(new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(30)));

        SubTask updateSubTask = new SubTask(2, "Обновлённая под задача 1", Status.DONE,
                "Обновлённое описание подзадачи 4",
                1, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30));

        String subTaskJson = gson.toJson(updateSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertEquals(201, response.statusCode());
        assertEquals("Обновлённая под задача 1", inMemoryTaskManager.getByIdSubTasks(2).getName(),
                "Название задачи не обновилось.");
        assertEquals("Обновлённое описание подзадачи 4", inMemoryTaskManager.getByIdSubTasks(2).getDescription(),
                "Описание задачи не обновилось.");
        assertEquals(Status.DONE, inMemoryTaskManager.getByIdSubTasks(2).getStatus(), "Статус задачи не обновился.");
        assertEquals(LocalDateTime.of(2025, 5, 25, 10, 0),
                inMemoryTaskManager.getByIdSubTasks(2).getStartTime(), "Время начала задачи не обновилось.");
        assertEquals(Duration.ofMinutes(30), inMemoryTaskManager.getByIdSubTasks(2).getDuration(),
                "Продолжение задачи не обновилось.");
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createSubtask(new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30)));
        inMemoryTaskManager.createSubtask(new SubTask(3, "Под задача 2", Status.DONE, "Описание подзадачи 2",
                1, LocalDateTime.of(2026, 5, 25, 10, 0),
                Duration.ofMinutes(30)));
        inMemoryTaskManager.createSubtask(new SubTask(4, "Под задача 4", Status.IN_PROGRESS, "Описание подзадачи 3",
                1, LocalDateTime.of(2027, 5, 25, 10, 0),
                Duration.ofMinutes(30)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<SubTask> subTasksFromManager = inMemoryTaskManager.getAllSubTask();

        List<SubTask> subTasksFromResponse = gson.fromJson(response.body(), new SubTasksListTypeToken().getType());

        assertEquals(subTasksFromManager, subTasksFromResponse, "Получены не все задачи.");
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(3, subTasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubTaskId() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createSubtask(new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        SubTask subTaskResponse = gson.fromJson(response.body(), SubTask.class);

        List<SubTask> subTasksFromManager = inMemoryTaskManager.getAllSubTask();

        assertEquals(subTaskResponse, inMemoryTaskManager.getByIdSubTasks(2));
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(subTaskResponse, "Задача не возвращается");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createSubtask(new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<SubTask> subTasksFromManager = inMemoryTaskManager.getAllSubTask();

        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertTrue(subTasksFromManager.isEmpty(), "Задача не удалились.");
    }

    @Test
    public void timeCrossingTest() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        SubTask subTask1 = new SubTask(2, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                1, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(90));

        SubTask subTask2 = new SubTask(3, "Под задача 2", Status.NEW, "Описание подзадачи 2",
                1, LocalDateTime.of(2025, 5, 25, 11, 0),
                Duration.ofMinutes(90));

        SubTask subTask3 = new SubTask(4, "Под задача 3", Status.NEW, "Описание подзадачи 3",
                1, LocalDateTime.of(2025, 5, 25, 15, 0),
                Duration.ofMinutes(90));

        SubTask subTask4 = new SubTask(5, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                1, LocalDateTime.of(2025, 5, 25, 16, 0),
                Duration.ofMinutes(90));

        SubTask subTask5 = new SubTask(6, "Под задача 5", Status.NEW, "Описание подзадачи 5",
                1, LocalDateTime.of(2025, 5, 25, 20, 0),
                Duration.ofMinutes(90));

        String subTask1Json = gson.toJson(subTask1);
        String subTask2Json = gson.toJson(subTask2);
        String subTask3Json = gson.toJson(subTask3);
        String subTask4Json = gson.toJson(subTask4);
        String subTask5Json = gson.toJson(subTask5);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask3Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask4Json)).build();
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask5Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<SubTask> tasksFromManager = inMemoryTaskManager.getAllSubTask();

        assertEquals(201, response1.statusCode(), "Неверный код ответа.");
        assertEquals(406, response2.statusCode(), "Найдено пересечение.");
        assertEquals(201, response3.statusCode(), "Неверный код ответа.");
        assertEquals(406, response4.statusCode(), "Найдено пересечение.");
        assertEquals(201, response5.statusCode(), "Неверный код ответа.");
        assertEquals("Под задача 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Под задача 3", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Под задача 5", tasksFromManager.get(2).getName(), "Некорректное имя задачи");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        assertFalse(tasksFromManager.contains(subTask2), "Задача с пересечением добавилась.");
        assertFalse(tasksFromManager.contains(subTask4), "Задача с пересечением добавилась.");
        assertTrue(tasksFromManager.contains(subTask1));
        assertTrue(tasksFromManager.contains(subTask3));
        assertTrue(tasksFromManager.contains(subTask5));
    }
}

