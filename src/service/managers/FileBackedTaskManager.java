package service.managers;

import exception.ManagerSaveException;
import model.enums.Status;
import model.enums.Type;
import model.tasks.Epic;
import model.tasks.SubTask;
import model.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final int TASK_WITH_TIME_LENGTH = 6;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (Reader fileReader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals("id,type,name,status,description,epic,startTime,duration")) {
                    continue;
                }

                Task task = fileBackedTaskManager.fromString(line);
                fileBackedTaskManager.idGenerate();

                if (Type.TASK == task.getType()) {
                    Task task1 = fileBackedTaskManager.fromString(line);
                    fileBackedTaskManager.tasks.put(task.getId(), task1);
                }
                if (Type.EPIC == task.getType()) {
                    Epic epic = (Epic) fileBackedTaskManager.fromString(line);
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                }
                if (Type.SUBTASK == task.getType()) {
                    SubTask subTask = (SubTask) fileBackedTaskManager.fromString(line);
                    fileBackedTaskManager.subTasks.put(subTask.getId(), subTask);
                    Epic epic = fileBackedTaskManager.epics.get(subTask.getIdEpic());
                    epic.getSubTaskEpicsId().add(subTask.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBackedTaskManager;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
             BufferedWriter br = new BufferedWriter(fileWriter)) {

            br.write("id,type,name,status,description,epic,startTime,duration\n");

            for (Task task : tasks.values()) {
                br.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                br.write(toString(epic) + "\n");
            }
            for (SubTask subTask : subTasks.values()) {
                br.write(toString(subTask) + "\n");
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения.");
        }
    }

    public String toString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",")
                .append(task.getType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus());

        if (task.getType() == Type.TASK) {
            builder.append(",").append(task.getDescription());
        }

        if (task.getType() == Type.SUBTASK) {
            builder.append(",").append(task.getDescription()).append(",").append(((SubTask) task).getIdEpic());
        }

        if (task.getStartTime() != null) {
            builder.append(",").append(task.getStartTime()).append(",").append(task.getDuration());
        }
        return builder.toString();
    }

    public Task fromString(String value) {
        String[] taskSplit = value.split(",");

        if (Type.TASK == Type.valueOf(taskSplit[1])) {
            if (taskSplit.length > TASK_WITH_TIME_LENGTH) {
                return new Task(Integer.parseInt(taskSplit[0]), taskSplit[2], Status.valueOf(taskSplit[3]), taskSplit[4],
                        LocalDateTime.parse(taskSplit[5]), Duration.parse(taskSplit[6]));
            }
            return new Task(Integer.parseInt(taskSplit[0]), taskSplit[2], Status.valueOf(taskSplit[3]), taskSplit[4]);
        }

        if (Type.EPIC == Type.valueOf(taskSplit[1])) {
            return new Epic(Integer.parseInt(taskSplit[0]), taskSplit[2], Status.valueOf(taskSplit[3]));
        }

        if (Type.SUBTASK == Type.valueOf(taskSplit[1])) {
            if (taskSplit.length > TASK_WITH_TIME_LENGTH) {
                return new SubTask(Integer.parseInt(taskSplit[0]), taskSplit[2], Status.valueOf(taskSplit[3]),
                        taskSplit[4], Integer.parseInt(taskSplit[5]), LocalDateTime.parse(taskSplit[6]),
                        Duration.parse(taskSplit[7]));
            }
            return new SubTask(Integer.parseInt(taskSplit[0]), taskSplit[2], Status.valueOf(taskSplit[3]),
                    taskSplit[4], Integer.parseInt(taskSplit[5]));
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(SubTask subTask) {
        super.createSubtask(subTask);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

}
