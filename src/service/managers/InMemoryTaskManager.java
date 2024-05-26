package service.managers;

import exception.TimeOverlapException;
import model.tasks.Epic;
import model.enums.Status;
import model.tasks.SubTask;
import model.tasks.Task;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected int id = 1;
    HistoryManager historyManager = Managers.getDefaultHistory();

    protected Set<Task> taskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(taskSet);
    }

    public boolean startCheckTaskOverlap(Task task) {
        return taskSet.stream().noneMatch(taskIn -> checkTaskOverlap(taskIn, task));
    }

    public boolean checkTaskOverlap(Task taskInSet, Task task) {
        return taskInSet.getEndTime().isAfter(task.getStartTime()) && task.getEndTime().isAfter(taskInSet.getStartTime());
    }

    public void addDurationToStartDateTime(Epic epic) {
        if (epic.getStartTime() != null) {
            Optional<LocalDateTime> startTime = epic.getSubTaskEpicsId().stream()
                    .map(subTaskId -> subTasks.get(subTaskId))
                    .map(Optional::ofNullable)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Task::getStartTime)
                    .min(Comparator.naturalOrder());

            Optional<LocalDateTime> endTime = epic.getSubTaskEpicsId().stream()
                    .map(subTaskId -> subTasks.get(subTaskId))
                    .map(Optional::ofNullable)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Task::getEndTime)
                    .max(Comparator.naturalOrder());

            startTime.ifPresent(epic::setStartTime);
            endTime.ifPresent(epic::setEndTime);
            if (startTime.isPresent() && endTime.isPresent()) {
                epic.setDuration(Duration.between(startTime.get(), endTime.get()));
            }
        }
    }

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
        tasks.values().stream()
                .map(Task::getId)
                .forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subTasks.values().stream()
                .map(SubTask::getId)
                .forEach(historyManager::remove);

        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);

        subTasks.clear();
        taskSet.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.values().stream()
                .map(SubTask::getId)
                .forEach(historyManager::remove);

        subTasks.clear();

        epics.keySet().stream()
                .map(epics::get)
                .forEach(epic -> {
                    epic.getSubTaskEpicsId().clear();
                    checkStatus(epic);
                });
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

        if (task.getStartTime() == null) {
            tasks.put(task.getId(), task);
        }
        if (task.getStartTime() != null) {
            if (startCheckTaskOverlap(task)) {
                taskSet.add(task);
                tasks.put(task.getId(), task);
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idGenerate());
        if (epic.getEndTime() != null) {
            addDurationToStartDateTime(epic);
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(SubTask subTask) {
        subTask.setId(idGenerate());
        Epic epic = epics.get(subTask.getIdEpic());
        if (subTask.getStartTime() == null) {
            subTasks.put(subTask.getId(), subTask);
            epic.getSubTaskEpicsId().add(subTask.getId());
        }

        if (subTask.getStartTime() != null) {
            if (startCheckTaskOverlap(subTask)) {
                taskSet.add(subTask);
                subTasks.put(subTask.getId(), subTask);
                epic.getSubTaskEpicsId().add(subTask.getId());
            } else {
                throw new TimeOverlapException("Добавление невозможно, найдено пересечение по времени.");
            }
        }
        addDurationToStartDateTime(epics.get(subTask.getIdEpic()));
        checkStatus(epics.get(subTask.getIdEpic()));
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println(" ID - " + task.getId() + ", в Задачах отсутствует. Возможные варианты ID - " + tasks.keySet());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Integer> newEpic = epics.get(epic.getId()).getSubTaskEpicsId();
            epic.setSubTaskEpicsId(newEpic);
            epics.put(epic.getId(), epic);
            checkStatus(epic);
            addDurationToStartDateTime(epic);
        } else {
            System.out.println(" ID - " + epic.getId() + ", в Эпиках отсутствует. Возможные варианты ID - " + epics.keySet());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            if (subTask.getStartTime() == null) {
                subTasks.put(subTask.getId(), subTask);
            }

            if (subTask.getStartTime() != null) {
                if (startCheckTaskOverlap(subTask)) {
                    taskSet.add(subTask);
                    subTasks.put(subTask.getId(), subTask);
                    addDurationToStartDateTime(epics.get(subTask.getIdEpic()));
                } else {
                    throw new TimeOverlapException("Добавление невозможно, найдено пересечение по времени.");
                }
            }
            checkStatus(epics.get(subTask.getIdEpic()));
        } else {
            System.out.println(" ID - " + id + ", в Эпиках отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Введенный вами id - " + id + ", в Задачах отсутствует. Возможные варианты ID - " + subTasks.keySet());
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
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
            Epic epic = epics.get(subTasks.get(id).getIdEpic());

            epic.getSubTaskEpicsId().stream()
                    .filter(subTaskId -> subTaskId == id)
                    .findFirst()
                    .ifPresent(subTaskId -> epic.getSubTaskEpicsId().remove(subTaskId));

            addDurationToStartDateTime(epic);
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
        return epic.getSubTaskEpicsId().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    public void checkStatus(Epic epic) {
        boolean isNew = epic.getSubTaskEpicsId().stream()
                .map(id -> subTasks.get(id).getStatus())
                .anyMatch(status -> status == Status.NEW);

        boolean isInProgress = epic.getSubTaskEpicsId().stream()
                .map(id -> subTasks.get(id).getStatus())
                .anyMatch(status -> status == Status.IN_PROGRESS);

        boolean isDone = epic.getSubTaskEpicsId().stream()
                .map(id -> subTasks.get(id).getStatus())
                .anyMatch(status -> status == Status.DONE);

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
