package service.http.handlers;

import com.google.gson.Gson;
import model.enums.Status;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.http.HttpTaskServer;
import service.http.adapters.EpicsListTypeToken;
import service.http.adapters.SubTasksListTypeToken;
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

class HistoryHandlerTest {
    TaskManager inMemoryTaskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = httpTaskServer.getGson();

    HistoryHandlerTest() throws IOException {
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
    public void getHistoryTest() throws IOException, InterruptedException {
        inMemoryTaskManager.createTask(new Task("Задача 1", "Описание задачи 1",
                Status.NEW, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(60)));
        inMemoryTaskManager.createTask(new Task("Задача 2", "Описание задачи 2",
                Status.NEW, LocalDateTime.of(2024, 6, 25, 10, 0),
                Duration.ofMinutes(60)));
        inMemoryTaskManager.createTask(new Task("Задача 3", "Описание задачи 3",
                Status.NEW, LocalDateTime.of(2024, 7, 25, 10, 0),
                Duration.ofMinutes(60)));

        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createEpic(new Epic("Эпик 2"));
        inMemoryTaskManager.createEpic(new Epic("Эпик 3"));

        inMemoryTaskManager.createSubtask(new SubTask(7, "Под задача 1", Status.NEW, "Описание подзадачи 1",
                4, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30)));
        inMemoryTaskManager.createSubtask(new SubTask(8, "Под задача 2", Status.DONE, "Описание подзадачи 2",
                5, LocalDateTime.of(2026, 5, 25, 10, 0),
                Duration.ofMinutes(30)));
        inMemoryTaskManager.createSubtask(new SubTask(9, "Под задача 4", Status.IN_PROGRESS, "Описание подзадачи 3",
                6, LocalDateTime.of(2027, 5, 25, 10, 0),
                Duration.ofMinutes(30)));

        inMemoryTaskManager.getByIdTasks(2);
        inMemoryTaskManager.getByIdSubTasks(7);
        inMemoryTaskManager.getByIdEpics(5);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Task> tasksFromManager = inMemoryTaskManager.getHistory();

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        List<SubTask> subTasksFromResponse = gson.fromJson(response.body(), new SubTasksListTypeToken().getType());
        List<Epic> epicsFromResponse22 = gson.fromJson(response.body(), new EpicsListTypeToken().getType());

        List<Task> all = List.of(tasksFromResponse.getFirst(),
                subTasksFromResponse.get(1),
                epicsFromResponse22.get(2));

        assertEquals(tasksFromManager, all, "Получены не все задачи.");
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
    }
}