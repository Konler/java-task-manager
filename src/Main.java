import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class Main {

public static void main(String[]args) throws IOException, ManagerSaveException {

//    TaskManager in = new InMemoryTaskManager();
//
//    Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
//    in.createTask(task);
//
//    Task task1 = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567992l), Duration.ofMinutes(5));
//    in.createTask(task);
//
    HttpTaskServer taskServer = new HttpTaskServer();
    taskServer.startServer();

}
}

//    public static void main(String[] args) throws IOException, ManagerSaveException {
//        TaskManager taskManager = Managers.getDefault();
//
//        Task task1 = new Task("Task1", "Описание task1", Status.NEW);
//        taskManager.createTask(task1);
//
//        Task task2 = new Task("Task2", "Описание task2", Status.IN_PROGRESS);
//        taskManager.createTask(task2);
//
//        Epic epic1 = new Epic("Epic 1", "Описание эпика 1", Status.NEW);
//        taskManager.createEpic(epic1);
//
//        SubTask subTask1Epic1 = new SubTask("subTask1Epic1", "Description subTask1Epic1",1, Status.NEW);
//        subTask1Epic1.setEpicId(epic1.getId());
//        taskManager.createSubTask(subTask1Epic1);
//
//        SubTask subTask2Epic1 = new SubTask("subTask2Epic1", "Description subTask2Epic1",1, Status.IN_PROGRESS);
//        subTask2Epic1.setEpicId(epic1.getId());
//        taskManager.createSubTask(subTask2Epic1);
//
//        SubTask subTask3Epic1 = new SubTask("subTask3Epic1", "Description subTask3Epic1",1, Status.DONE);
//        subTask3Epic1.setEpicId(epic1.getId());
//        taskManager.createSubTask(subTask3Epic1);
//
//
//        Epic epic2 = new Epic("Epic 2", "Описание эпика 2", Status.NEW);
//        taskManager.createEpic(epic2);
//
//
//        printTasks(taskManager.getAllTasks());
//        printEpics(taskManager.getAllEpics());
//        printSubTasks(taskManager.getAllSubTasks());
//
//        subTask3Epic1.setStatus(Status.NEW);
//        taskManager.updateSubTask(subTask3Epic1);
//        printEpics(taskManager.getAllEpics());
//
//        taskManager.getTaskById(task2.getId());
//        taskManager.getEpicById(epic1.getId());
//        taskManager.getSubTaskById(subTask2Epic1.getId());
//        taskManager.getTaskById(task2.getId());
//        taskManager.getEpicById(epic1.getId());
//
//
//        System.out.println("История до удалений");
//        printHistory(taskManager.getHistory());
//
//        taskManager.deleteTaskById(task1.getId());
//        printTasks(taskManager.getAllTasks());
//        taskManager.deleteEpicById(epic2.getId());
//        printEpics(taskManager.getAllEpics());
//
//
//        taskManager.deleteTaskById(task2.getId());
//        printHistory(taskManager.getHistory());
//        taskManager.deleteEpicById(epic1.getId());
//
//        taskManager.deleteAllTasks();
//        System.out.println("Удалили таски.");
//        printHistory(taskManager.getHistory());
//        taskManager.deleteAllSubTasks();
//        System.out.println("Удалили сабтаски.");
//        printHistory(taskManager.getHistory());
//        taskManager.deleteAllEpics();
//        System.out.println("Удалили эпики.");
//        printHistory(taskManager.getHistory());


