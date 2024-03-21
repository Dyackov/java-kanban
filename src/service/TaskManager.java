package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int id = 0;

    public int idGenerate() {
        return id++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
        System.out.println("Все эпики, а так же подзадачи относящиеся к ним подзадачи удалены.");
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Integer i : epics.keySet()) {
            epics.get(i).getSubTaskEpicsId().clear();
        }
        System.out.println("Все подзадачи эпиков удалены.");
    }

    public void printByIdTasks(int id) {
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
        } else {
            System.out.println("Задачи с ID " + id + ", не существует." + " Возможные варианты ID - " + tasks.keySet());
        }
    }

    public void printByIdEpics(int id) {
        if (epics.containsKey(id)) {
            System.out.println(epics.get(id));
        } else {
            System.out.println("Эпика с ID " + id + ", не существует." + " Возможные варианты ID - " + epics.keySet());
        }
    }

    public void printByIdSubTasks(int id) {
        if (subTasks.containsKey(id)) {
            System.out.println(subTasks.get(id));
        } else {
            System.out.println("Подзадачи с ID " + id + ", не существует." + " Возможные варианты ID - " + subTasks.keySet());
        }
    }

    public void createTask(Task task) {
        task.setId(idGenerate());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(idGenerate());
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(SubTask subTask) {
        subTask.setId(idGenerate());
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getIdEpic());
        epic.getSubTaskEpicsId().add(subTask.getId());

        checkStatus(epics.get(subTask.getIdEpic()));
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            System.out.println("Задача: " + tasks.get(id).getName() + " - обновлена.");
            task.setId(id);
            tasks.put(task.getId(), task);
        } else {
            System.out.println(" ID - " + id + ", в Задачах отсутствует. Возможные варианты ID - " + tasks.keySet());
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            System.out.println("Эпик: " + epics.get(id).getName() + " - обновлён.");
            ArrayList<Integer> newEpic = epics.get(id).getSubTaskEpicsId();
            epic.setSubTaskEpicsId(newEpic);

            epic.setId(id);
            epics.put(epic.getId(), epic);

            checkStatus(epic);
        } else {
            System.out.println(" ID - " + id + ", в Эпиках отсутствует. Возможные варианты ID - " + epics.keySet());
        }
    }

    public void updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id)) {
            System.out.println("Подзадача: " + subTasks.get(id).getName() + " - обновлена.");
            subTask.setId(id);
            subTasks.put(subTask.getId(), subTask);

            checkStatus(epics.get(subTask.getIdEpic()));
        } else {
            System.out.println(" ID - " + id + ", в Эпиках отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println("Задача: " + tasks.get(id).getName() + " - удалена.");
            tasks.remove(id);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Задачах отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            System.out.println("Эпик: " + epics.get(id).getName() + " - удалён.");
            Epic epic = epics.get(id);

            for (Integer i : epic.getSubTaskEpicsId()) {
                subTasks.remove(i);
            }
            epics.remove(id);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Эпиках отсутствует.");
        }
    }

    public void deleteSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            System.out.println("Подзадача: " + subTasks.get(id).getName() + " - удалена.");

            Epic epic = epics.get(subTasks.get(id).getIdEpic());

            if (epic.getSubTaskEpicsId().contains(id)) {
                for (int i = 0; i < epic.getSubTaskEpicsId().size(); i++) {
                    if (epic.getSubTaskEpicsId().get(i) == id) {
                        epic.getSubTaskEpicsId().remove(i);
                        break;
                    }
                }
            }
            subTasks.remove(id);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Подзадачах отсутствует.");
        }
    }

    public void getSubtasksForEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        for (Integer i : epic.getSubTaskEpicsId()) {
            System.out.println(subTasks.get(i));
        }
    }

    public void checkStatus(Epic epic) {
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;

        for (Integer id : epic.getSubTaskEpicsId()) {
            if (subTasks.get(id).getStatus() == Status.NEW) {
                isNew = true;
            } else if (subTasks.get(id).getStatus() == Status.IN_PROGRESS) {
                isInProgress = true;
            } else if (subTasks.get(id).getStatus() == Status.DONE) {
                isDone = true;
            }
        }

        if (isNew && !isInProgress && !isDone) {
            epic.setStatus(Status.NEW);
        } else if (isDone && !isNew && !isInProgress) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "\n" +
                "ЭПИК=" + epics +
                "\n" +
                "ЗАДАЧА=" + tasks +
                "\n" +
                "ПОД ЗАДАЧА=" + subTasks +
                '}';
    }
}
