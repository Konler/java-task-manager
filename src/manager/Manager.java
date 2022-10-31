package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int idGenerator;

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {

        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        for (Integer subTask : subTasks.keySet()) {
            deleteSubTaskById(subTask);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    public Task getTaskById(Integer taskId) {
        return tasks.get(taskId);
    }

    public SubTask getSubTaskById(Integer subTaskId) {
        return subTasks.get(subTaskId);
    }

    public Epic getEpicById(Integer epicId) {
        return epics.get(epicId);
    }

    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setId(getNextId());
            epics.get(subTask.getEpicId()).getSubTaskIds().add(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
            resolveEpicNewStatus(epics.get(subTask.getEpicId()));
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTaskIds().add(subTask.getId());
        } else {
            System.out.println("SubTask не может быть создана без Epic");
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Task с id:" + task.getId() + " не найден");
        }
    }

    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey((subtask.getId()))) {
            subTasks.put(subtask.getId(), subtask);
            resolveEpicNewStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("SubTask с id:" + subtask.getId() + " не найден");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey((epic.getId()))) {
            epics.put(epic.getId(), epic);
            resolveEpicNewStatus(epic);
        } else {
            System.out.println("Epic с id:" + epic.getId() + " не найден");
        }
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicById(Integer idEpic) {
        deleteAllSubTaskByEpicId(idEpic);
        epics.remove(idEpic);
    }

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

    public ArrayList<SubTask> getEpicSubTasksByEpicId(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
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
