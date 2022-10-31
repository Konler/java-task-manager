import manager.Manager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager tasksManager = new Manager();

        Task task1 = new Task("Task1", "Описание task1", Status.NEW);
        tasksManager.createTask(task1);

        Task task2 = new Task("Task2", "Описание task2", Status.IN_PROGRESS);
        tasksManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Описание эпика 1", Status.NEW);
        tasksManager.createEpic(epic1);

        SubTask subTask1Epic1 = new SubTask("subTask1Epic1", "Description subTask1Epic1", Status.NEW);
        subTask1Epic1.setEpicId(epic1.getId());
        tasksManager.createSubTask(subTask1Epic1);

        SubTask subTask2Epic1 = new SubTask("subTask2Epic1", "Description subTask2Epic1", Status.IN_PROGRESS);
        subTask2Epic1.setEpicId(epic1.getId());
        tasksManager.createSubTask(subTask2Epic1);

        Epic epic2 = new Epic("Epic 2", "Описание эпика 2", Status.NEW);
        tasksManager.createEpic(epic2);

        SubTask subTask1Epic2 = new SubTask("subTask1Epic2", "Description subTask1Epic2", Status.DONE);
        subTask1Epic2.setEpicId(epic2.getId());
        tasksManager.createSubTask(subTask1Epic2);

        printTasks(tasksManager.getAllTasks());
        printEpics(tasksManager.getAllEpics());
        printSubTasks(tasksManager.getAllSubTasks());

        subTask1Epic2.setStatus(Status.NEW);
        tasksManager.updateSubTask(subTask1Epic2);
        printEpics(tasksManager.getAllEpics());

        tasksManager.deleteTaskById(task1.getId());
        printTasks(tasksManager.getAllTasks());
        tasksManager.deleteEpicById(epic2.getId());
        printEpics(tasksManager.getAllEpics());
    }


    private static void printTasks(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println("Имя задачи: " + task.getName() + "; Id задачи: " + task.getId() + "; Описание задачи: "
                    + task.getDescription() + "; Статус задачи: " + task.getStatus());
        }
    }

    private static void printSubTasks(ArrayList<SubTask> subTasks) {
        for (SubTask subtask : subTasks) {
            System.out.println("Имя подзадачи: " + subtask.getName() + "; Id подзадачи: " + subtask.getId()
                    + "; Описание подзадачи: " + subtask.getDescription() + "; Статус подзадачи: " + subtask.getStatus());
        }
    }

    private static void printEpics(ArrayList<Epic> epics) {
        for (Epic epic : epics) {
            System.out.println("Имя эпика: " + epic.getName() + "; Id эпика: " + epic.getId()
                    + "; Описание эпика: " + epic.getDescription() + "; Статус эпика: " + epic.getStatus());
        }
    }
}
