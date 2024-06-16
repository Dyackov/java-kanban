package model.tasks;

import model.enums.Status;
import model.enums.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskEpicsId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, Status status) {
        super(id, name, status);
    }

    public Epic(String name) {
        super(name);
        setStatus(Status.NEW);
    }

    public Epic(String name, int id) {
        super(name, id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setSubTaskEpicsId(ArrayList<Integer> subTaskEpicsId) {
        this.subTaskEpicsId = subTaskEpicsId;
    }

    public ArrayList<Integer> getSubTaskEpicsId() {
        return this.subTaskEpicsId;
    }

    @Override
    public String toString() {
        return "Эпик:" + getName() + '\'' +
                " id: " + getId() + '\'' +
                " Статус: " + getStatus() + "\n" +
                " Время начала: " + getStartTime() + "\n" +
                " Продолжительность: " + getDuration() +
                " Окончание времени: " + getEndTime();
    }

    public Type getType() {
        return Type.EPIC;
    }
}