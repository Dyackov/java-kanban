package model.tasks;

import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(int id, String name, Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // для Main с датой
    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    // для FileBackedTaskManager c Датой
    public Task(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
    }

    // для FileBackedTaskManager без Даты
    public Task(int id, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Task(String name) {
        this.name = name;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        if (duration == null) {
            throw new NullPointerException("Ошибка. Длительность равна 0.");
        }
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Задача:" + getName() + '\'' +
                " Описание задачи:" + getDescription() + '\'' +
                " id:" + getId() + '\'' +
                " Статус: " + getStatus() + "\n";
    }

}