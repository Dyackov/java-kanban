package service.managers;

import exception.ManagerSaveException;
import model.tasks.Epic;
import model.enums.Status;
import model.tasks.SubTask;
import model.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void setUp() {
        try {
            File file = File.createTempFile("testBackend", ".tmp");
            manager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }

    @Test
    void loadFromFileTest() {
        try {
            File file = File.createTempFile("fileManager", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            fileBackedTaskManager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
            fileBackedTaskManager.createEpic(new Epic("Эпик 4"));
            fileBackedTaskManager.createSubtask(new SubTask("Под задача 7", "Описание подзадачи 1", Status.NEW, 2));

            FileBackedTaskManager fileBackedTaskManagerTwo = FileBackedTaskManager.loadFromFile(file);

            assertNotNull(fileBackedTaskManagerTwo.tasks);
            assertNotNull(fileBackedTaskManagerTwo.epics);
            assertNotNull(fileBackedTaskManagerTwo.subTasks);
            assertEquals(4, fileBackedTaskManagerTwo.id);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла.");
        }

    }

    @Test
    void saveTest() {

        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            fileBackedTaskManager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
            fileBackedTaskManager.createEpic(new Epic("Эпик 4"));
            fileBackedTaskManager.createSubtask(new SubTask("Под задача 7", "Описание подзадачи 1", Status.NEW, 2));

            List<String> expected = new ArrayList<>();
            expected.add("id,type,name,status,description,epic,startTime,duration");
            expected.add("1,TASK,задача 1,NEW,описание задачи 1");
            expected.add("2,EPIC,Эпик 4,NEW");
            expected.add("3,SUBTASK,Под задача 7,NEW,Описание подзадачи 1,2");

            Reader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(fileReader);

            List<String> actual = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();
                actual.add(line);
            }
            br.close();
            assertEquals(expected, actual);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в создании файла.");
        }
    }
}