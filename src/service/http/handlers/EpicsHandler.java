package service.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NullException;
import exception.TimeOverlapException;
import model.enums.Endpoint;
import model.tasks.Epic;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    static final int ARRAYS_ID = 3;
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
            default:
                sendNotFound(exchange, "Неверный запрос.");

        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length < ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
            sendText(exchange, gson.toJson(manager.getAllEpics()));
        } else if (path.length == ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            Epic epic = manager.getByIdEpics(Integer.parseInt(path[2]));
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange, "Эпика с ID - " + path[2] + " не существует.");
            }
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + "/" + path[3] + " запроса от клиента.");
            try {
                sendText(exchange, gson.toJson(manager.getSubtasksForEpic(Integer.parseInt(path[2]))));
            } catch (NullException nullException) {
                sendNotFound(exchange, "Эпика с ID - " + path[2] + " не существует.");
            }
        }
    }

    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (path.length < ARRAYS_ID) {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
            try {
                manager.createEpic(epic);
                sendNoText(exchange);
            } catch (TimeOverlapException timeOverlapException) {
                sendHasInteractions(exchange, timeOverlapException.getMessage());
            }
        } else {
            System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
            try {
                epic.setId(Integer.parseInt(path[2]));
                manager.updateEpic(epic);
                sendNoText(exchange);
            } catch (NullException nullException) {
                sendNotFound(exchange, "Эпика с ID - " + path[2] + " не существует.");
            }
        }
    }

    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + "/" + path[2] + " запроса от клиента.");
        try {
            manager.deleteEpicById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (NullException nullException) {
            sendNotFound(exchange, "Эпика с ID - " + path[2] + " не существует.");
        }
    }
}




