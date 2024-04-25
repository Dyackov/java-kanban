package test.service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.Node;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager = Managers.getDefaultHistory();
    static TaskManager manager = Managers.getDefault();

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

    @Test
    void getHistory() {
        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        task1.setId(1);
        Epic epic1 = new Epic("Эпик 1");
        epic1.setId(2);
        SubTask subTask1 = new SubTask("Под задача 7", "Описание подзадачи 1", Status.NEW, 2);
        subTask1.setId(3);

        historyManager.addHistory(task1);
        historyManager.addHistory(epic1);
        historyManager.addHistory(subTask1);

        assertNotNull(historyManager.getHistory());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(epic1, historyManager.getHistory().get(1));
        assertEquals(subTask1, historyManager.getHistory().get(2));
    }
}