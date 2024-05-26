package model.tasks;

import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int idEpic;

    // для FileBackedTaskManager c Датой
    public SubTask(int id, String name, Status status, String description, int idEpic, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
        this.idEpic = idEpic;
    }

    // для FileBackedTaskManager без Даты
    public SubTask(int id, String name, Status status, String description, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    // для Main
    public SubTask(String name, String description, Status status, int idEpic, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String descriptionTask, Status status, int idEpic) {
        super(name, descriptionTask, status);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, int id, Status status, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public SubTask(String name, int idEpic) {
        super(name);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int id) {
        idEpic = id;
    }

    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "Подзадача: " + getName() + '\'' +
                " Описание: " + getDescription() + '\'' +
                " id: " + getId() + '\'' +
                " Статус: " + getStatus() + "\n";
    }

}

