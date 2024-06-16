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

class PrioritizedHandlerTest {
    TaskManager inMemoryTaskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = httpTaskServer.getGson();

    PrioritizedHandlerTest() throws IOException {
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
    public void getPrioritizedTest() throws IOException, InterruptedException {
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
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getPrioritizedTasks();

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TasksListTypeToken().getType());

        assertEquals(tasksFromManager, tasksFromResponse, "Получены не все задачи.");
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
    }
}