package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> history = new HashMap<>();

    private Node<Task> head = null;
    private Node<Task> tail = null;

    private Node<Task> linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> node = new Node<>(tail, task, null);
        tail = node;
        if (oldTail == null) {
            head = node;
        } else {
            oldTail.next = node;
        }
        return node;
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> node = linkLast(task);
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        history.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node<Task> task) {
        Node<Task> removeNode = task;
        Node<Task> prev = removeNode.prev;
        Node<Task> next = removeNode.next;
        if (prev == null) {
            head = next;
            next.prev = null;
        } else if (next == null) {
            tail = prev;
            prev.next = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>();
        Node<Task> node = head;

        while (node != null) {
            historyTasks.add(node.data);
            node = node.next;
        }
        return historyTasks;
    }

    @Override
    public void remove(int id) {
        if ( history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }
}
