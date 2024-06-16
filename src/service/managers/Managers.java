package service.managers;

import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileBacked() {
        return new FileBackedTaskManager(new File("resources\\fileManager.csv"));
    }

}
