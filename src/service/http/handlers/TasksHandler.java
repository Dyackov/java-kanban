package service.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NullException;
import exception.TimeOverlapException;
import model.enums.Endpoint;
import model.tasks.Task;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    static final int ARRAYS_ID = 2;
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());

        switch (endpoint) {
            case GET:
                handleGet(exchange);
                break;
            case POST:
                handlePost(exchange);
                break;
            case DELETE:
                handleDelete(exchange);
                break;
            default:
                sendNotFound(exchange, "Неверный запрос.");

        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length <= ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
            if (manager.getAllTasks().isEmpty()) {
                sendNotFound(exchange, "Задачи ещё не созданы.");
            } else {
                sendText(exchange, gson.toJson(manager.getAllTasks()));
            }
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            Task task = manager.getByIdTasks(Integer.parseInt(path[2]));
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange, "Задачи с ID - " + path[2] + " не существует.");
            }
        }
    }


    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (path.length <= ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
            try {
                manager.createTask(task);
                sendNoText(exchange);
            } catch (TimeOverlapException timeOverlapException) {
                sendHasInteractions(exchange, timeOverlapException.getMessage());
            }
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            try {
                task.setId(Integer.parseInt(path[2]));
                manager.updateTask(task);
                sendNoText(exchange);
            } catch (TimeOverlapException timeOverlapException) {
                sendHasInteractions(exchange, timeOverlapException.getMessage());
            } catch (NullException nullException) {
                sendNotFound(exchange, "Задачи с ID - " + path[2] + " не существует.");
            }

        }
    }

    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
        try {
            manager.deleteTaskById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (NullException nullException) {
            sendNotFound(exchange, "Задачи с ID - " + path[2] + " не существует.");
        }
    }
}

