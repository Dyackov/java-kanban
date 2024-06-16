package service.http.handlers;

import com.google.gson.Gson;
import model.tasks.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.http.HttpTaskServer;
import service.http.adapters.EpicsListTypeToken;
import service.interfaces.TaskManager;
import service.managers.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest {
    TaskManager inMemoryTaskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
    Gson gson = httpTaskServer.getGson();

    EpicsHandlerTest() throws IOException {
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
    public void testPostEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1");

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Epic> epicsFromManager = inMemoryTaskManager.getAllEpics();

        assertEquals(201, response.statusCode(), "Неверный код ответа.");
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество Эпиков");
        assertEquals("Эпик 1", epicsFromManager.getFirst().getName(), "Некорректное имя Эпика");
    }

    @Test
    public void testPostUpdateEpic() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));

        Epic updateEpic = new Epic("Обновлённый эпик 1");

        String taskJson = gson.toJson(updateEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertEquals(201, response.statusCode(), "Неверный код ответа.");
        assertEquals("Обновлённый эпик 1", inMemoryTaskManager.getByIdEpics(1).getName(),
                "Название эпика не обновилось.");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));
        inMemoryTaskManager.createEpic(new Epic("Эпик 2"));
        inMemoryTaskManager.createEpic(new Epic("Эпик 3"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Epic> epicsFromManager = inMemoryTaskManager.getAllEpics();

        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicsListTypeToken().getType());

        assertEquals(epicsFromManager, epicsFromResponse, "Получены не все задачи.");
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(3, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicId() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Epic> epicsFromManager = inMemoryTaskManager.getAllEpics();

        Epic epicResponse = gson.fromJson(response.body(), Epic.class);

        assertEquals(epicResponse, inMemoryTaskManager.getByIdEpics(1));
        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertNotNull(epicsFromManager, "Эпик не возвращается");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Эпик 1"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        List<Epic> epicsFromManager = inMemoryTaskManager.getAllEpics();

        assertEquals(200, response.statusCode(), "Неверный код ответа.");
        assertTrue(epicsFromManager.isEmpty(), "Задачи не удалились.");
    }
}