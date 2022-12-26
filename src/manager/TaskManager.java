package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
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

    Task getTaskById(Integer taskId) throws IOException, ManagerSaveException;

    SubTask getSubTaskById(Integer subTaskId) throws IOException, ManagerSaveException;

    Epic getEpicById(Integer epicId) throws IOException, ManagerSaveException;

    void createTask(Task task) throws IOException, ManagerSaveException;

    void createEpic(Epic epic) throws IOException, ManagerSaveException;

    void createSubTask(SubTask subTask) throws IOException, ManagerSaveException;

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer idEpic);

    void deleteSubTaskById(Integer subTaskId) throws IOException, ManagerSaveException;

    ArrayList<SubTask> getEpicSubTasksByEpicId(Integer epicId);

}

