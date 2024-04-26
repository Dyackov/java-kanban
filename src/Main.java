import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("задача 1", "описание задачи 1", Status.NEW));
        manager.createTask(new Task("задача 2", "описание задачи 2", Status.DONE));
        manager.createTask(new Task("задача 3", "описание задачи 3", Status.IN_PROGRESS));

        manager.createEpic(new Epic("Эпик 4"));
        manager.createEpic(new Epic("Эпик 5"));
        manager.createEpic(new Epic("Эпик 6"));

        manager.createSubtask(new SubTask("Под задача 7", "Описание подзадачи 1", Status.NEW, 4));
        manager.createSubtask(new SubTask("Под задача 8", "Описание подзадачи 2", Status.NEW, 5));
        manager.createSubtask(new SubTask("Под задача 9", "Описание подзадачи 3", Status.NEW, 6));


        manager.getByIdTasks(1);
        manager.getByIdTasks(2);
        manager.getByIdTasks(3);

        manager.getByIdEpics(4);
        manager.getByIdEpics(5);
        manager.getByIdEpics(6);

        manager.getByIdSubTasks(7);
        manager.getByIdSubTasks(8);
        manager.getByIdSubTasks(9);

        manager.deleteAllSubTasks();

        System.out.println(manager.getHistory());


    }
}
