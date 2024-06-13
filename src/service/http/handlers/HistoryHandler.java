package service.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.enums.Endpoint;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");
        System.out.println("Началась обработка " + exchange.getRequestMethod() + " /" + path[1] + " запроса от клиента.");
        if (Objects.requireNonNull(endpoint) == Endpoint.GET) {
            sendText(exchange, gson.toJson(manager.getHistory()));

        } else {
            sendNotFound(exchange, "Неверный запрос.");
        }
    }
}




