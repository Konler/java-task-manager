package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    Task getTaskById(Integer taskId);

    SubTask getSubTaskById(Integer subTaskId);

    Epic getEpicById(Integer epicId);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer idEpic);

    void deleteSubTaskById(Integer subTaskId);

    ArrayList<SubTask> getEpicSubTasksByEpicId(Integer epicId);

}

