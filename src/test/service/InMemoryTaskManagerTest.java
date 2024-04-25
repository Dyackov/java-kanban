package test.service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InMemoryTaskManagerTest {

    static TaskManager manager = Managers.getDefault();

    @BeforeAll
    public static void setUp() {
        manager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
        manager.createTask(new Task("задача 2", "описание задачи 2", Status.DONE));
        manager.createTask(new Task("задача 3", "описание задачи 3", Status.IN_PROGRESS));

        manager.createEpic(new Epic("Эпик 1"));
        manager.createEpic(new Epic("Эпик 2"));
        manager.createEpic(new Epic("Эпик 3"));

        manager.createSubtask(new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4));
        manager.createSubtask(new SubTask("Под задача 2", "Описание подзадачи 2", Status.NEW, 5));
        manager.createSubtask(new SubTask("Под задача 3", "Описание подзадачи 3", Status.NEW, 6));

    }

    @Test
    @Order(4)
    public void getAllTaskTest() {
        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("задача 2", "описание задачи 2", Status.DONE);
        task2.setId(2);

        Task task3 = new Task("задача 3", "описание задачи 3", Status.IN_PROGRESS);
        task3.setId(3);

        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task1);
        expectedTask.add(task2);
        expectedTask.add(task3);

        assertEquals(expectedTask.size(), manager.getAllTasks().size(), "Количество задач не совпадает.");
        assertEquals(expectedTask, manager.getAllTasks(), "Получены не все задачи.");
    }

    @Test
    @Order(5)
    public void getAllEpicsTest() {
        Epic epic1 = new Epic("Эпик 1");
        epic1.setId(4);
        Epic epic2 = new Epic("Эпик 2");
        epic2.setId(5);
        Epic epic3 = new Epic("Эпик 3");
        epic3.setId(6);

        List<Epic> expectedEpic = new ArrayList<>();
        expectedEpic.add(epic1);
        expectedEpic.add(epic2);
        expectedEpic.add(epic3);

        assertEquals(expectedEpic.size(), manager.getAllEpics().size(), "Количество эпиков не совпадает.");
        assertEquals(expectedEpic, manager.getAllEpics(), "Получены не все эпики.");
    }

    @Test
    @Order(6)
    public void getAllSubTaskTest() {
        SubTask subTask1 = new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4);
        subTask1.setId(7);
        SubTask subTask2 = new SubTask("Под задача 2", "Описание подзадачи 2", Status.NEW, 5);
        subTask2.setId(8);
        SubTask subTask3 = new SubTask("Под задача 3", "Описание подзадачи 3", Status.NEW, 6);
        subTask3.setId(9);

        List<SubTask> expectedSubTasks = new ArrayList<>();
        expectedSubTasks.add(subTask1);
        expectedSubTasks.add(subTask2);
        expectedSubTasks.add(subTask3);

        assertEquals(expectedSubTasks.size(), manager.getAllSubTask().size(), "Количество подзадач не совпадает.");
        assertEquals(expectedSubTasks, manager.getAllSubTask(), "Получены не все подзадачи.");
    }

    @Test
    @Order(1)
    public void createTaskTest() {
        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        task1.setId(1);
        assertEquals(task1, manager.getByIdTasks(1), "Задача не создалась.");
    }

    @Test
    @Order(2)
    public void createEpicTest() {
        Epic epic1 = new Epic("Эпик 1");
        epic1.setId(4);
        assertEquals(epic1, manager.getByIdEpics(4), "Эпик не создался.");
    }

    @Test
    @Order(3)
    public void createSubTaskTest() {
        SubTask subTask1 = new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4);
        subTask1.setId(7);
        assertEquals(subTask1, manager.getByIdSubTasks(7), "Эпик не создался.");
    }

    @Test
    public void getByIdTasksTest() {
        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        task1.setId(1);
        assertEquals(task1, manager.getByIdTasks(1), "Такой задачи не существует.");
    }

    @Test
    @Order(7)
    public void getByIdEpicsTest() {
        Epic epic1 = new Epic("Эпик 1");
        epic1.setId(4);
        assertEquals(epic1, manager.getByIdEpics(4), "Такого эпика не существует.");
    }

    @Test
    public void getByIdSubTasksTest() {
        SubTask subTask1 = new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4);
        subTask1.setId(7);
        assertEquals(subTask1, manager.getByIdSubTasks(7), "Такой подзадачи не существует.");
    }

    @Test
    public void updateTaskTest() {
        Task task1 = new Task("Обновлённая задача 1", "Обновлённое описание задачи 1", 1, Status.DONE);
        manager.updateTask(task1);
        assertEquals(task1, manager.getByIdTasks(1), "Задача не обновлена.");
    }

    @Test
    public void updateEpicTest() {
        Epic epic1 = new Epic("Обновлённый эпик 1", 4);
        manager.updateEpic(epic1);
        assertEquals(epic1, manager.getByIdEpics(4), "Эпик не обновлён.");
    }

    @Test
    public void updateSubTaskTest() {
        SubTask subTask1 = new SubTask("Обнов.Под задача 1", "Обнов.Описание подзадачи 1", 7,
                Status.NEW, 4);
        manager.updateSubTask(subTask1);
        assertEquals(subTask1, manager.getByIdSubTasks(7), "Подзадача не обновлена.");
    }

    @Test
    public void getSubtasksForEpicTest() {
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(new SubTask("Под задача 1", "Описание подзадачи 1", 7, Status.NEW, 4));
        assertEquals(subTasks, manager.getSubtasksForEpic(4), "Получены не все подзадачи эпика.");
    }

    @Test
    public void checkStatusTest() {
        manager.updateSubTask(new SubTask("Обнов.Под задача 2", "Обнов.Описание подзадачи 2", 8,
                Status.IN_PROGRESS, 5));
        assertEquals(manager.getByIdEpics(5).getStatus(), Status.IN_PROGRESS, "Статус не обновился.");


    }

    @Test
    public void deleteAllTaskTest() {
        TaskManager deleteManager = Managers.getDefault();

        deleteManager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
        deleteManager.createTask(new Task("задача 2", "описание задачи 2", Status.DONE));
        deleteManager.createTask(new Task("задача 3", "описание задачи 3", Status.IN_PROGRESS));

        deleteManager.createEpic(new Epic("Эпик 1"));
        deleteManager.createEpic(new Epic("Эпик 2"));
        deleteManager.createEpic(new Epic("Эпик 3"));

        deleteManager.createSubtask(new SubTask("Под задача 1", "Описание подзадачи 1", Status.NEW, 4));
        deleteManager.createSubtask(new SubTask("Под задача 2", "Описание подзадачи 2", Status.NEW, 5));
        deleteManager.createSubtask(new SubTask("Под задача 3", "Описание подзадачи 3", Status.NEW, 6));

        deleteManager.deleteTaskById(1);
        assertNull(deleteManager.getByIdTasks(1), "Задача по ID не удалена.");

        deleteManager.deleteSubtaskById(7);
        assertNull(deleteManager.getByIdSubTasks(7), "Подзадача по ID не удалена.");

        deleteManager.deleteEpicById(4);
        assertNull(deleteManager.getByIdEpics(4), "Эпик по ID не удалён.");

        assertNotNull(deleteManager.getAllTasks(), "Список задач пуст.");
        deleteManager.deleteAllTasks();
        assertTrue(deleteManager.getAllTasks().isEmpty(), "Задачи не удалены.");

        assertNotNull(deleteManager.getAllSubTask(), "Список подзадач пуст.");
        deleteManager.deleteAllSubTasks();
        assertTrue(deleteManager.getAllSubTask().isEmpty(), "Подзадачи не удалены");

        assertNotNull(deleteManager.getAllEpics(), "Список эпиков пуст.");
        deleteManager.deleteAllEpics();
        assertTrue(deleteManager.getAllEpics().isEmpty(), "Эпики не удалены.");
    }
}