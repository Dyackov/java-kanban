package service.interfaces;

import model.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addHistory(Task task);

    List<Task> getHistory();

    void remove(int id);

}
