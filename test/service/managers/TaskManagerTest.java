package service.managers;

import model.enums.Status;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    void addDefaultTasks() {
        manager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
        manager.createTask(new Task("задача 2", "описание задачи 2", Status.DONE));
        manager.createTask(new Task("задача 3", "описание задачи 3", Status.IN_PROGRESS));

        manager.createEpic(new Epic("Эпик 1"));
        manager.createEpic(new Epic("Эпик 2"));
        manager.createEpic(new Epic("Эпик 3"));

        manager.createSubtask(new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4));
        manager.createSubtask(new SubTask("Под задача 2", "Описание подзадачи 2", Status.NEW, 5));
        manager.createSubtask(new SubTask("Под задача 3", "Описание подзадачи 3", Status.NEW, 6));

        manager.createSubtask(new SubTask("Под задача 4", "Описание подзадачи 4", Status.NEW,
                4, LocalDateTime.of(2024, 5, 25, 10, 0),
                Duration.ofMinutes(30)));
        manager.createSubtask(new SubTask("Под задача 5", "Описание подзадачи 5", Status.NEW,
                5, LocalDateTime.of(2024, 5, 25, 15, 0),
                Duration.ofMinutes(30)));
        manager.createSubtask(new SubTask("Под задача 6", "Описание подзадачи 6", Status.NEW,
                6, LocalDateTime.of(2024, 5, 25, 20, 0),
                Duration.ofMinutes(30)));
    }

    @Test
    public void testException() {
        addDefaultTasks();
        Assertions.assertThrows(RuntimeException.class, () -> manager.createSubtask(new SubTask("Под задача 6", "Описание подзадачи 6", Status.NEW,
                4, LocalDateTime.of(2024, 5, 25, 20, 10),
                Duration.ofMinutes(30))), "Пересечение не должно приводить к исключению");

        Assertions.assertDoesNotThrow(() -> manager.createSubtask(new SubTask("Под задача 6", "Описание подзадачи 6", Status.NEW,
                4, LocalDateTime.of(2025, 5, 25, 20, 10),
                Duration.ofMinutes(30))), "Пересечение не должно приводить к исключению");
    }

    @Test
    void getPrioritizedTasks() {
        addDefaultTasks();

        List<Task> expectedTask = List.of(
                new SubTask(10, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                        10, LocalDateTime.of(2024, 5, 25, 10, 0),
                        Duration.ofMinutes(30)),
                new SubTask(11, "Под задача 5", Status.NEW, "Описание подзадачи 5",
                        10, LocalDateTime.of(2024, 5, 25, 15, 0),
                        Duration.ofMinutes(30)),
                new SubTask(12, "Под задача 6", Status.NEW, "Описание подзадачи 6",
                        10, LocalDateTime.of(2024, 5, 25, 20, 0),
                        Duration.ofMinutes(30)));

        assertEquals(expectedTask.size(), manager.getPrioritizedTasks().size(), "Количество задач не совпадает.");
        assertEquals(expectedTask, manager.getPrioritizedTasks(), "Получены не все задачи.");
    }

    @Test
    void getAllTasks() {
        addDefaultTasks();

        List<Task> expectedTask = List.of(
                new Task("задача 1", "описание задачи 1", 1, Status.NEW),
                new Task("задача 2", "описание задачи 2", 2, Status.DONE),
                new Task("задача 3", "описание задачи 3", 3, Status.IN_PROGRESS));

        assertEquals(expectedTask.size(), manager.getAllTasks().size(), "Количество задач не совпадает.");
        assertEquals(expectedTask, manager.getAllTasks(), "Получены не все задачи.");
    }

    @Test
    void getAllEpics() {
        addDefaultTasks();

        List<Epic> expectedEpic = List.of(
                new Epic(4, "Эпик 1", Status.NEW),
                new Epic(5, "Эпик 2", Status.NEW),
                new Epic(6, "Эпик 3", Status.NEW));

        assertEquals(expectedEpic.size(), manager.getAllEpics().size(), "Количество эпиков не совпадает.");
        assertEquals(expectedEpic, manager.getAllEpics(), "Получены не все эпики.");
    }

    @Test
    void getAllSubTask() {
        addDefaultTasks();

        List<SubTask> expectedSubTasks = List.of(
                new SubTask("Под задача 1", "Описание подзадачи 1", 7, Status.NEW, 4),
                new SubTask("Под задача 2", "Описание подзадачи 2", 8, Status.NEW, 5),
                new SubTask("Под задача 3", "Описание подзадачи 3", 9, Status.NEW, 6),
                new SubTask(10, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                        10, LocalDateTime.of(2024, 5, 25, 10, 0),
                        Duration.ofMinutes(30)),
                new SubTask(11, "Под задача 5", Status.NEW, "Описание подзадачи 5",
                        10, LocalDateTime.of(2024, 5, 25, 15, 0),
                        Duration.ofMinutes(30)),
                new SubTask(12, "Под задача 6", Status.NEW, "Описание подзадачи 6",
                        10, LocalDateTime.of(2024, 5, 25, 20, 0),
                        Duration.ofMinutes(30)));

        assertEquals(expectedSubTasks.size(), manager.getAllSubTask().size(), "Количество подзадач не совпадает.");
        assertEquals(expectedSubTasks, manager.getAllSubTask(), "Получены не все подзадачи.");
    }

    @Test
    void deleteAllTasks() {
        addDefaultTasks();
        manager.deleteAllTasks();
        assertNotNull(manager.getAllTasks(), "Список задач пуст.");
        assertTrue(manager.getAllTasks().isEmpty(), "Задачи не удалены.");
    }

    @Test
    void deleteAllEpics() {
        addDefaultTasks();

        manager.deleteAllEpics();
        assertNotNull(manager.getAllEpics(), "Список эпиков пуст.");
        assertTrue(manager.getAllEpics().isEmpty(), "Эпики не удалены.");
    }

    @Test
    void deleteAllSubTasks() {
        addDefaultTasks();

        manager.deleteAllSubTasks();
        assertNotNull(manager.getAllSubTask(), "Список подзадач пуст.");
        assertTrue(manager.getAllSubTask().isEmpty(), "Подзадачи не удалены");
    }

    @Test
    void getByIdTasks() {
        addDefaultTasks();
        Task task1 = new Task("задача 1", "описание задачи 1", 1, Status.NEW);
        Task task2 = new Task("задача 2", "описание задачи 2", 2, Status.DONE);
        Task task3 = new Task("задача 3", "описание задачи 3", 3, Status.IN_PROGRESS);
        assertEquals(task1, manager.getByIdTasks(1), "Такой задачи не существует.");
        assertEquals(task2, manager.getByIdTasks(2), "Такой задачи не существует.");
        assertEquals(task3, manager.getByIdTasks(3), "Такой задачи не существует.");
    }

    @Test
    void getByIdEpics() {
        addDefaultTasks();
        Epic epic1 = new Epic(4, "Эпик 1", Status.NEW);
        Epic epic2 = new Epic(5, "Эпик 2", Status.NEW);
        Epic epic3 = new Epic(6, "Эпик 3", Status.NEW);
        assertEquals(epic1, manager.getByIdEpics(4), "Такого эпика не существует.");
        assertEquals(epic2, manager.getByIdEpics(5), "Такого эпика не существует.");
        assertEquals(epic3, manager.getByIdEpics(6), "Такого эпика не существует.");
        assertFalse(manager.getAllEpics().isEmpty(), "Эпики не создались.");
    }

    @Test
    void getByIdSubTasks() {
        addDefaultTasks();

        List<SubTask> expectedSubTasks = List.of(
                new SubTask("Под задача 1", "Описание подзадачи 1", 7, Status.NEW, 4),
                new SubTask("Под задача 2", "Описание подзадачи 2", 8, Status.NEW, 5),
                new SubTask("Под задача 3", "Описание подзадачи 3", 9, Status.NEW, 6),
                new SubTask(10, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                        10, LocalDateTime.of(2024, 5, 25, 10, 0),
                        Duration.ofMinutes(30)),
                new SubTask(11, "Под задача 5", Status.NEW, "Описание подзадачи 5",
                        10, LocalDateTime.of(2024, 5, 25, 15, 0),
                        Duration.ofMinutes(30)),
                new SubTask(12, "Под задача 6", Status.NEW, "Описание подзадачи 6",
                        10, LocalDateTime.of(2024, 5, 25, 20, 0),
                        Duration.ofMinutes(30)));

        assertEquals(expectedSubTasks.size(), manager.getAllSubTask().size(), "Количество подзадач не совпадает.");
        assertEquals(expectedSubTasks, manager.getAllSubTask(), "Получены не все подзадачи.");
        assertNotNull(manager.getByIdSubTasks(7), "Такой подзадачи не существует.");
        assertNotNull(manager.getByIdSubTasks(8), "Такой подзадачи не существует.");
        assertNotNull(manager.getByIdSubTasks(9), "Такой подзадачи не существует.");
        assertFalse(manager.getAllSubTask().isEmpty(), "Подзадачи не создались.");
    }

    @Test
    void createTask() {
        addDefaultTasks();
        Task task1 = new Task("задача 1", "описание задачи 1", 1, Status.NEW);
        Task task2 = new Task("задача 2", "описание задачи 2", 2, Status.DONE);
        Task task3 = new Task("задача 3", "описание задачи 3", 3, Status.IN_PROGRESS);
        assertEquals(task1, manager.getByIdTasks(1), "Задача не создалась.");
        assertEquals(task2, manager.getByIdTasks(2), "Задача не создалась.");
        assertEquals(task3, manager.getByIdTasks(3), "Задача не создалась.");

        assertNotNull(manager.getByIdTasks(1), "Задача не создалась.");
        assertNotNull(manager.getByIdTasks(2), "Задача не создалась.");
        assertNotNull(manager.getByIdTasks(3), "Задача не создалась.");
        assertFalse(manager.getAllTasks().isEmpty(), "Задачи не создались.");
    }

    @Test
    void createEpic() {
        addDefaultTasks();
        Epic epic1 = new Epic(4, "Эпик 1", Status.NEW);
        Epic epic2 = new Epic(5, "Эпик 2", Status.NEW);
        Epic epic3 = new Epic(6, "Эпик 3", Status.NEW);
        assertEquals(epic1, manager.getByIdEpics(4), "Эпик не создался.");
        assertEquals(epic2, manager.getByIdEpics(5), "Эпик не создался.");
        assertEquals(epic3, manager.getByIdEpics(6), "Эпик не создался.");

        assertNotNull(manager.getByIdEpics(4), "Эпик не создался.");
        assertNotNull(manager.getByIdEpics(5), "Эпик не создался.");
        assertNotNull(manager.getByIdEpics(6), "Эпик не создался.");
        assertFalse(manager.getAllEpics().isEmpty(), "Эпики не создались.");
    }

    @Test
    void createSubtask() {
        addDefaultTasks();
        List<SubTask> expectedSubTasks = List.of(
                new SubTask("Под задача 1", "Описание подзадачи 1", 7, Status.NEW, 4),
                new SubTask("Под задача 2", "Описание подзадачи 2", 8, Status.NEW, 5),
                new SubTask("Под задача 3", "Описание подзадачи 3", 9, Status.NEW, 6),
                new SubTask(10, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                        10, LocalDateTime.of(2024, 5, 25, 10, 0),
                        Duration.ofMinutes(30)),
                new SubTask(11, "Под задача 5", Status.NEW, "Описание подзадачи 5",
                        10, LocalDateTime.of(2024, 5, 25, 15, 0),
                        Duration.ofMinutes(30)),
                new SubTask(12, "Под задача 6", Status.NEW, "Описание подзадачи 6",
                        10, LocalDateTime.of(2024, 5, 25, 20, 0),
                        Duration.ofMinutes(30)));

        assertEquals(expectedSubTasks.size(), manager.getAllSubTask().size(), "Количество подзадач не совпадает.");
        assertEquals(expectedSubTasks, manager.getAllSubTask(), "Создались не все подзадачи.");
        assertNotNull(manager.getByIdSubTasks(7), "Подзадача не создалась.");
        assertNotNull(manager.getByIdSubTasks(8), "Подзадача не создалась.");
        assertNotNull(manager.getByIdSubTasks(9), "Подзадача не создалась.");
        assertFalse(manager.getAllSubTask().isEmpty(), "Подзадачи не создались.");
    }

    @Test
    void updateTask() {
        addDefaultTasks();
        Task task1 = new Task("Обновлённая задача 1", "Обновлённое описание задачи 1", 1, Status.DONE);
        Task task2 = new Task("Обновлённая задача 2", "Обновлённое описание задачи 2", 2, Status.DONE);
        Task task3 = new Task("Обновлённая задача 3", "Обновлённое описание задачи 3", 3, Status.DONE);
        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateTask(task3);
        assertEquals(task1, manager.getByIdTasks(1), "Задача не обновлена.");
        assertEquals(task2, manager.getByIdTasks(2), "Задача не обновлена.");
        assertEquals(task3, manager.getByIdTasks(3), "Задача не обновлена.");

    }

    @Test
    void updateEpic() {
        addDefaultTasks();
        Epic epic1 = new Epic("Обновлённый эпик 1", 4);
        Epic epic2 = new Epic("Обновлённый эпик 2", 5);
        Epic epic3 = new Epic("Обновлённый эпик 3", 6);
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);
        manager.updateEpic(epic3);
        assertEquals(epic1, manager.getByIdEpics(4), "Эпик не обновлён.");
        assertEquals(epic2, manager.getByIdEpics(5), "Эпик не обновлён.");
        assertEquals(epic3, manager.getByIdEpics(6), "Эпик не обновлён.");
    }

    @Test
    void updateSubTask() {
        addDefaultTasks();
        SubTask subTask1 = new SubTask("Обнов.Под задача 1", "Обнов.Описание подзадачи 1", 7,
                Status.NEW, 4);
        SubTask subTask2 = new SubTask("Обнов.Под задача 2", "Обнов.Описание подзадачи 2", 8,
                Status.NEW, 5);
        SubTask subTask3 = new SubTask("Обнов.Под задача 3", "Обнов.Описание подзадачи 3", 9,
                Status.NEW, 6);

        SubTask subTask4 = new SubTask(10, "Обнов.Под задача 4", Status.NEW, "Обнов.Описание подзадачи 4",
                4, LocalDateTime.of(2025, 5, 25, 10, 0),
                Duration.ofMinutes(30));
        SubTask subTask5 = new SubTask(11, "Обнов.Под задача 5", Status.NEW, "Обнов.Описание подзадачи 5",
                5, LocalDateTime.of(2025, 5, 25, 15, 0),
                Duration.ofMinutes(30));
        SubTask subTask6 = new SubTask(12, "Обнов.Под задача 6", Status.NEW, "Обнов.Описание подзадачи 6",
                6, LocalDateTime.of(2025, 5, 25, 20, 0),
                Duration.ofMinutes(30));

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);
        manager.updateSubTask(subTask4);
        manager.updateSubTask(subTask5);
        manager.updateSubTask(subTask6);
        assertEquals(subTask1, manager.getByIdSubTasks(7), "Подзадача не обновлена.");
        assertEquals(subTask2, manager.getByIdSubTasks(8), "Подзадача не обновлена.");
        assertEquals(subTask3, manager.getByIdSubTasks(9), "Подзадача не обновлена.");
        assertEquals(subTask4, manager.getByIdSubTasks(10), "Подзадача не обновлена.");
        assertEquals(subTask5, manager.getByIdSubTasks(11), "Подзадача не обновлена.");
        assertEquals(subTask6, manager.getByIdSubTasks(12), "Подзадача не обновлена.");
    }

    @Test
    void deleteTaskById() {
        addDefaultTasks();
        manager.deleteTaskById(1);
        manager.deleteTaskById(2);
        manager.deleteTaskById(3);
        assertNull(manager.getByIdTasks(1), "Задача по ID не удалена.");
        assertNull(manager.getByIdTasks(2), "Задача по ID не удалена.");
        assertNull(manager.getByIdTasks(3), "Задача по ID не удалена.");
    }

    @Test
    void deleteEpicById() {
        addDefaultTasks();
        manager.deleteEpicById(4);
        manager.deleteEpicById(5);
        manager.deleteEpicById(6);
        assertNull(manager.getByIdEpics(4), "Эпик по ID не удалена.");
        assertNull(manager.getByIdEpics(5), "Эпик по ID не удалена.");
        assertNull(manager.getByIdEpics(6), "Эпик по ID не удалена.");
    }

    @Test
    void deleteSubtaskById() {
        addDefaultTasks();
        manager.deleteSubtaskById(7);
        manager.deleteSubtaskById(8);
        manager.deleteSubtaskById(9);
        assertNull(manager.getByIdSubTasks(7), "Подзадача по ID не удалена.");
        assertNull(manager.getByIdSubTasks(8), "Подзадача по ID не удалена.");
        assertNull(manager.getByIdSubTasks(9), "Подзадача по ID не удалена.");
    }

    @Test
    void getSubtasksForEpic() {
        addDefaultTasks();

        List<SubTask> expectedSubTasks = List.of(
                new SubTask("Под задача 1", "Описание подзадачи 1", 7, Status.NEW, 4),
                new SubTask(10, "Под задача 4", Status.NEW, "Описание подзадачи 4",
                        4, LocalDateTime.of(2024, 5, 25, 10, 0),
                        Duration.ofMinutes(30)));

        assertEquals(expectedSubTasks, manager.getSubtasksForEpic(4), "Получены не все подзадачи эпика.");
    }

    @Test
    public void checkStatusTest() {

        manager.createEpic(new Epic("Эпик а"));
        manager.createSubtask(new SubTask("Под задача a.1", "Описание подзадачи a.1", Status.NEW, 1));
        manager.createSubtask(new SubTask("Под задача a.2", "Описание подзадачи a.2", Status.NEW, 1));
        manager.createSubtask(new SubTask("Под задача a.3", "Описание подзадачи a.3", Status.NEW, 1));
        manager.createSubtask(new SubTask("Под задача a.4", "Описание подзадачи a.4", Status.NEW, 1));
        assertEquals(Status.NEW, manager.getByIdEpics(1).getStatus());

        manager.createEpic(new Epic("Эпик b"));
        manager.createSubtask(new SubTask("Под задача b.1", "Описание подзадачи b.1", Status.DONE, 6));
        manager.createSubtask(new SubTask("Под задача b.2", "Описание подзадачи b.2", Status.DONE, 6));
        manager.createSubtask(new SubTask("Под задача b.3", "Описание подзадачи b.3", Status.DONE, 6));
        manager.createSubtask(new SubTask("Под задача b.4", "Описание подзадачи b.4", Status.DONE, 6));
        assertEquals(Status.DONE, manager.getByIdEpics(6).getStatus());

        manager.createEpic(new Epic("Эпик c"));
        manager.createSubtask(new SubTask("Под задача c.1", "Описание подзадачи c.1", Status.NEW, 11));
        manager.createSubtask(new SubTask("Под задача c.2", "Описание подзадачи c.2", Status.DONE, 11));
        manager.createSubtask(new SubTask("Под задача c.3", "Описание подзадачи c.3", Status.NEW, 11));
        manager.createSubtask(new SubTask("Под задача c.4", "Описание подзадачи c.4", Status.IN_PROGRESS, 11));
        assertEquals(Status.IN_PROGRESS, manager.getByIdEpics(11).getStatus());

        manager.createEpic(new Epic("Эпик d"));
        manager.createSubtask(new SubTask("Под задача d.1", "Описание подзадачи d.1", Status.IN_PROGRESS, 16));
        manager.createSubtask(new SubTask("Под задача d.2", "Описание подзадачи d.2", Status.IN_PROGRESS, 16));
        manager.createSubtask(new SubTask("Под задача d.3", "Описание подзадачи d.3", Status.IN_PROGRESS, 16));
        manager.createSubtask(new SubTask("Под задача d.4", "Описание подзадачи d.4", Status.IN_PROGRESS, 16));
        assertEquals(Status.IN_PROGRESS, manager.getByIdEpics(16).getStatus());

        manager.updateSubTask(new SubTask("Обнов.Под задача 2", "Обнов.Описание подзадачи 2", 2,
                Status.IN_PROGRESS, 1));
        assertEquals(manager.getByIdEpics(16).getStatus(), Status.IN_PROGRESS, "Статус не обновился.");
    }
}