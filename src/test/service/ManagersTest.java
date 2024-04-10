package test.service;

import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
    }

}