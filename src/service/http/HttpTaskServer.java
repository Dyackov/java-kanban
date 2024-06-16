package service.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;
import service.http.handlers.*;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager managersInMemory;
    private final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter());
    private final Gson gson = gsonBuilder.create();

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.managersInMemory = manager;
    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        httpServer.createContext("/tasks", new TasksHandler(managersInMemory, gson));
        httpServer.createContext("/epics", new EpicsHandler(managersInMemory, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(managersInMemory, gson));
        httpServer.createContext("/history", new HistoryHandler(managersInMemory, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(managersInMemory, gson));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
