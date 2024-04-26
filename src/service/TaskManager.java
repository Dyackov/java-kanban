package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTask();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task getByIdTasks(int id);

    Epic getByIdEpics(int id);

    SubTask getByIdSubTasks(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<SubTask> getSubtasksForEpic(int idEpic);

    List<Task> getHistory();

}
