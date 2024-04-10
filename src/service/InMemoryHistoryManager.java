package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();
    private static final int MAX_VALUE = 10;

    @Override
    public void addHistory(Task task) {
        if (history.size() == MAX_VALUE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return this.history;
    }

}
