package model;

public class SubTask extends Task {
    private int idEpic;

    public SubTask(String name, String descriptionTask, Status status, int idEpic) {
        super(name, descriptionTask, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int id) {
        idEpic = id;
    }

    @Override
    public String toString() {
        return "Подзадача: " + getName() + '\'' +
                " Описание: " + getDescription() + '\'' +
                " id: " + getId() + '\'' +
                " Статус: " + getStatus() + "\n";
    }
}

