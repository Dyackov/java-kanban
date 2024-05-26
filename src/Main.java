import service.managers.FileBackedTaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        File file = new File("src\\resources\\fileManager.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
    }
}
