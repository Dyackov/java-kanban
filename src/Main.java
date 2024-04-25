import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1");
        manager.createEpic(epic1);
        System.out.println(manager.getByIdEpics(1));






        SubTask subTask1 = new SubTask("Под задача 1", "Описание подзадачи 1", Status.DONE, 1);

        SubTask subTask2 = new SubTask("Под задача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 1);
       // subTask2.setId(8);

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        System.out.println(manager.getByIdEpics(1));
        manager.deleteAllSubTasks();
        System.out.println(manager.getByIdEpics(1));



    }
}
