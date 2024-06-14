package service.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskNotFoundException;
import exception.TimeOverlapException;
import model.enums.Endpoint;
import model.tasks.SubTask;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    static final int ARRAYS_ID = 2;
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());

        switch (endpoint) {
            case POST:
                handlePost(exchange);
                break;
            case GET:
                handleGet(exchange);
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
            sendText(exchange, gson.toJson(manager.getAllSubTask()));
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            SubTask subTask = manager.getByIdSubTasks(Integer.parseInt(path[2]));
            if (subTask != null) {
                sendText(exchange, gson.toJson(subTask));
            } else {
                sendNotFound(exchange, "Подзадачи с ID - " + path[2] + " не существует.");
            }
        }
    }

    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(body, SubTask.class);
        if (path.length <= ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
            try {
                manager.createSubtask(subTask);
                sendNoText(exchange);
            } catch (TimeOverlapException timeOverlapException) {
                sendHasInteractions(exchange, timeOverlapException.getMessage());
            }
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            try {
                subTask.setId(Integer.parseInt(path[2]));
                manager.updateSubTask(subTask);
                sendNoText(exchange);
            } catch (TimeOverlapException timeOverlapException) {
                sendHasInteractions(exchange, timeOverlapException.getMessage());
            } catch (TaskNotFoundException nullException) {
                sendNotFound(exchange, "Подзадачи с ID - " + path[2] + " не существует.");
            }
        }
    }

    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
        try {
            manager.deleteSubtaskById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (TaskNotFoundException nullException) {
            sendNotFound(exchange, "Задачи с ID - " + path[2] + " не существует.");
        }
    }
}