package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Epic> epics;
    private int idGenerator;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskById(Integer taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer subTask : subTasks.keySet()) {
            deleteSubTaskById(subTask);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setId(getNextId());
            epics.get(subTask.getEpicId()).getSubTaskIds().add(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
            resolveEpicNewStatus(epics.get(subTask.getEpicId()));
        } else {
            System.out.println("SubTask не может быть создана без Epic");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Task с id:" + task.getId() + " не найден");
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey((subtask.getId()))) {
            subTasks.put(subtask.getId(), subtask);
            resolveEpicNewStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("SubTask с id:" + subtask.getId() + " не найден");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey((epic.getId()))) {
            epics.put(epic.getId(), epic);
            resolveEpicNewStatus(epic);
        } else {
            System.out.println("Epic с id:" + epic.getId() + " не найден");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(Integer idEpic) {
        deleteAllSubTaskByEpicId(idEpic);
        epics.remove(idEpic);
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        SubTask subtask = getSubTaskById(subTaskId);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubTaskByIdFromEpic(subTaskId);
                subTasks.remove(subTaskId);
                resolveEpicNewStatus(epic);
            } else {
                System.out.println("Epic с id:" + subtask.getEpicId() + " не найден");
            }
        } else {
            System.out.println("SubTask с id:" + subTaskId + " не найден");
        }
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasksByEpicId(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Integer> subTaskIds = epic.getSubTaskIds();
            ArrayList<SubTask> epicSubTasks = new ArrayList<>();
            for (Integer subTaskId : subTaskIds) {
                if (subTasks.containsKey(subTaskId)) {
                    epicSubTasks.add(subTasks.get(subTaskId));
                } else {
                    System.out.println("SubTask c id:" + subTaskId + " не найден");
                }
            }
            return epicSubTasks;
        } else {
            System.out.println("Epic с id:" + epicId + " не найден");
        }
        return new ArrayList<>();
    }

    private Integer getNextId() {
        return idGenerator++;
    }

    private void deleteAllSubTaskByEpicId(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epic.getSubTaskIds().clear();
            resolveEpicNewStatus(epic);
        } else {
            System.out.println("Epic с id:" + epicId + " не найден");
        }
    }

    private void resolveEpicNewStatus(Epic epic) {
        boolean allNew = true;
        boolean allDone = true;
        boolean noSubTasks = epic.getSubTaskIds().isEmpty();

        if (noSubTasks) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask subtask : getEpicSubTasksByEpicId(epic.getId())) {
                allNew &= (subtask.getStatus() == Status.NEW);
                allDone &= (subtask.getStatus() == Status.DONE);
            }
            if (allNew) {
                epic.setStatus(Status.NEW);
            } else if (allDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}

