package service.managers;

import model.tasks.Epic;
import model.enums.Status;
import model.tasks.SubTask;
import model.tasks.Task;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.util.*;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected int id = 1;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    protected int idGenerate() {
        return id++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void deleteAllEpics() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        subTasks.clear();
        epics.clear();
        System.out.println("Все эпики, а так же подзадачи относящиеся к ним подзадачи удалены.");
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }

        subTasks.clear();
        for (Integer i : epics.keySet()) {
            epics.get(i).getSubTaskEpicsId().clear();
            checkStatus(epics.get(i));
        }
        System.out.println("Все подзадачи эпиков удалены.");
    }

    @Override
    public Task getByIdTasks(int id) {
        historyManager.addHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getByIdEpics(int id) {
        historyManager.addHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getByIdSubTasks(int id) {
        historyManager.addHistory(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        task.setId(idGenerate());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idGenerate());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(SubTask subTask) {
        subTask.setId(idGenerate());
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getIdEpic());
        epic.getSubTaskEpicsId().add(subTask.getId());

        checkStatus(epics.get(subTask.getIdEpic()));
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            System.out.println("Задача: " + task.getId() + " - обновлена.");
            tasks.put(task.getId(), task);
        } else {
            System.out.println(" ID - " + task.getId() + ", в Задачах отсутствует. Возможные варианты ID - " + tasks.keySet());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            System.out.println("Эпик: " + epic.getId() + " - обновлён.");
            ArrayList<Integer> newEpic = epics.get(epic.getId()).getSubTaskEpicsId();
            epic.setSubTaskEpicsId(newEpic);

            epics.put(epic.getId(), epic);

            checkStatus(epic);
        } else {
            System.out.println(" ID - " + epic.getId() + ", в Эпиках отсутствует. Возможные варианты ID - " + epics.keySet());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            System.out.println("Подзадача: " + subTask.getName() + " - обновлена.");
            subTasks.put(subTask.getId(), subTask);
            checkStatus(epics.get(subTask.getIdEpic()));
        } else {
            System.out.println(" ID - " + id + ", в Эпиках отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println("Задача: " + tasks.get(id).getName() + " - удалена.");
            tasks.remove(id);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Задачах отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
        historyManager.remove(id);
    }

    @Override
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
        historyManager.remove(id);
    }

    @Override
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
            checkStatus(epic);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Подзадачах отсутствует.");
        }
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getSubtasksForEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        List<SubTask> subTasksList = new ArrayList<>();
        for (Integer i : epic.getSubTaskEpicsId()) {
            subTasksList.add(subTasks.get(i));
        }
        return subTasksList;
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

        if (isDone && !isInProgress && !isNew) {
            epic.setStatus(Status.DONE);
        } else if (isInProgress || isDone) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return id == that.id && Objects.equals(tasks, that.tasks) && Objects.equals(subTasks, that.subTasks) && Objects.equals(epics, that.epics) && Objects.equals(historyManager, that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, subTasks, epics, id, historyManager);
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
