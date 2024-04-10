package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskEpicsId = new ArrayList<>();

    public Epic(String name) {
        super(name);
        setStatus(Status.NEW);
    }

    public Epic(String name, int id) {
        super(name, id);
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
                " Статус: " + getStatus() + "\n";
    }

}