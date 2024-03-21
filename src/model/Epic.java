package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskEpicsId = new ArrayList<>();

    public void setSubTaskEpicsId(ArrayList<Integer> subTaskEpicsId) {
        this.subTaskEpicsId = subTaskEpicsId;
    }

    public Epic(String name) {
        super(name);
        setStatus(Status.NEW);
    }

    public ArrayList<Integer> getSubTaskEpicsId() {
        return subTaskEpicsId;
    }

    public void delletesubTaskEpicsId(int id) {
        subTaskEpicsId.remove(id);
    }


    @Override
    public String toString() {
        return "Эпик:" + getName() + '\'' +
                " id: " + getId() + '\'' +
                " Статус: " + getStatus() + "\n";
    }
}
