import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager tasksManager=new Manager();

        Task task1=new Task();
        task1.name="Task1";
        task1.description="Описание task1";
        task1.status="NEW";
        tasksManager.createTask(task1);

        Task task2=new Task();
        task2.name="Task2";
        task2.description="Описание task2";
        task2.status="IN_PROGRESS";
        tasksManager.createTask(task2);

        Epic epic1=new Epic();
        epic1.name="Epic 1";
        epic1.description="Описание эпика 1";
        tasksManager.createEpic(epic1);

        Subtask subtask1Epic1=new Subtask();
        subtask1Epic1.name="subtask1Epic1";
        subtask1Epic1.description="Description subtask1Epic1";
        subtask1Epic1.status="NEW";
        subtask1Epic1.setEpicId(epic1.getId());
        tasksManager.createSubTask(subtask1Epic1);

        Subtask subtask2Epic1=new Subtask();
        subtask2Epic1.name="subtask2Epic1";
        subtask2Epic1.description="Description subtask2Epic1";
        subtask2Epic1.status="IN_PROGRESS";
        subtask2Epic1.setEpicId(epic1.getId());
        tasksManager.createSubTask(subtask2Epic1);

        Epic epic2=new Epic();
        epic2.name="Epic 2";
        epic2.description="Описание эпика 2";
        tasksManager.createEpic(epic2);

        Subtask subtask1Epic2=new Subtask();
        subtask1Epic2.name="subtask1Epic2";
        subtask1Epic2.description="Description subtask1Epic2";
        subtask1Epic2.status="DONE";
        subtask1Epic2.setEpicId(epic2.getId());
        tasksManager.createSubTask(subtask1Epic2);

        tasksManager.printTasks(tasksManager.getTasks());
        tasksManager.printEpiks(tasksManager.getEpics());
        tasksManager.printSubTasks(tasksManager.getSubTasks());

        subtask1Epic2.status="NEW";
        tasksManager.updateSubTask(subtask1Epic2);
        tasksManager.printEpiks(tasksManager.getEpics());

        tasksManager.deleteTaskById(task1.getId());
        tasksManager.printTasks(tasksManager.getTasks());
        tasksManager.deleteEpicById(epic2.getId());
        tasksManager.printEpiks(tasksManager.getEpics());




        }
}
