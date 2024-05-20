package service.managers;

import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

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