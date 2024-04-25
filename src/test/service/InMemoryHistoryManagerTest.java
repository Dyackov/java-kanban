package test.service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addHistoryTest() {
        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("задача 2", "описание задачи 2", Status.DONE);
        task2.setId(2);
        List<Task> historyActual = new ArrayList<>();
        historyActual.add(task1);
        historyActual.add(task2);

        historyManager.addHistory(task1);
        historyManager.addHistory(task2);

        assertEquals(historyActual.size(), historyManager.getHistory().size(), "Размер истории не совпадает");
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(historyActual, historyManager.getHistory(), "В историю не добавилось.");
    }
}