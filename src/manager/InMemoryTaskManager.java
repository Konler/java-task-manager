package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public final HashMap<Integer, Task> tasks;
    public final HashMap<Integer, SubTask> subTasks;
    public final HashMap<Integer, Epic> epics;
    private int idGenerator;
    public HistoryManager historyManager;

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public TreeSet<Task> prioritizedTaskSet = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)).
            thenComparing(Task::getId));

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskSet);
    }

    private boolean isCrossing(Task task) {
        for (Task t : prioritizedTaskSet) {
            if (task.getStartTime().isAfter(t.getStartTime()) && task.getStartTime().isBefore(t.getEndTime())) {
                return true;
            } else if (task.getEndTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime())) {
                return true;
            } else if (task.getStartTime().equals(t.getStartTime()) || task.getEndTime().equals(t.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskById(Integer taskId) throws ManagerSaveException {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) throws ManagerSaveException {
        if (subTasks.containsKey(subTaskId)) {
            historyManager.add(subTasks.get(subTaskId));
            return subTasks.get(subTaskId);
        }
        return null;
    }

    @Override
    public Epic getEpicById(Integer epicId) throws ManagerSaveException {
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
        if (!historyManager.getHistory().isEmpty()) {
            for (Task task : tasks.values()) {
                if (historyManager.getHistory().contains(task)) {
                    historyManager.remove(task.getId());
                }
            }
        }
        tasks.clear();

    }

    @Override
    public void deleteAllSubTasks() {
        final Set<Map.Entry<Integer, SubTask>> entries = subTasks.entrySet();
        final Iterator<Map.Entry<Integer, SubTask>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Integer, SubTask> entry = iterator.next();
            final Integer key = entry.getKey();
            iterator.remove();
            historyManager.remove(key);
        }
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!historyManager.getHistory().isEmpty()) {
            for (Task task : epics.values()) {
                if (historyManager.getHistory().contains(task)) {
                    historyManager.remove(task.getId());
                }
            }
        }
        epics.clear();
        deleteAllSubTasks();
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        task.setId(getNextId());
        if (task != null) {
            if (isCrossing(task)) {
                throw new ManagerSaveException("Пересечение задач!" + task.getId() + "не сохранена");
            }
            tasks.put(task.getId(), task);
            prioritizedTaskSet.add(task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        resolveEpicNewStatus(epic);
        resolveDurations(epic);
        resolveStartTime(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) throws ManagerSaveException {
        if (subTask != null) {
            if (epics.containsKey(subTask.getEpicId())) {
                subTask.setId(getNextId());
                epics.get(subTask.getEpicId()).getSubTaskIds().add(subTask.getId());
                if (subTask != null) {
                    if (isCrossing(subTask)) {
                        System.out.println("Пересечение задач!" + subTask);
                        return;
                    }
                    subTasks.put(subTask.getId(), subTask);
                    prioritizedTaskSet.add(subTask);
                } else {
                    System.out.println("Задача не найдена");
                }
                resolveEpicNewStatus(epics.get(subTask.getEpicId()));
                resolveDurations(epics.get(subTask.getEpicId()));
                resolveStartTime(epics.get(subTask.getEpicId()));
            } else {
                System.out.println("SubTask не может быть создана без Epic");
            }
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
            resolveDurations(epics.get(subtask.getEpicId()));
            resolveStartTime(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("SubTask с id:" + subtask.getId() + " не найден");
        }
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        if (epics.containsKey((epic.getId()))) {
            epics.put(epic.getId(), epic);
            resolveEpicNewStatus(epic);
            resolveDurations(epic);
            resolveStartTime(epic);
        } else {
            System.out.println("Epic с id:" + epic.getId() + " не найден");
        }

    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else {
            System.out.println("Id не верный");
        }
    }

    @Override
    public void deleteEpicById(Integer idEpic) {
        deleteAllSubTaskByEpicId(idEpic);
        historyManager.remove(idEpic);
        epics.remove(idEpic);
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) throws ManagerSaveException {
        SubTask subtask = getSubTaskById(subTaskId);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubTaskByIdFromEpic(subTaskId);
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
                resolveEpicNewStatus(epic);
                resolveDurations(epics.get(subtask.getEpicId()));
                resolveStartTime(epics.get(subtask.getEpicId()));
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
                historyManager.remove(subTaskId);
                subTasks.remove(subTaskId);
            }
            epic.getSubTaskIds().clear();
            resolveEpicNewStatus(epic);
            resolveDurations(epic);
            resolveStartTime(epic);
        } else {
            System.out.println("Epic с id:" + epicId + " не найден");
        }
    }

    public void resolveEpicNewStatus(Epic epic) {
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

    public void clearPrioritizedTask() {
        if (!prioritizedTaskSet.isEmpty()) {
            prioritizedTaskSet.clear();
        } else {
            throw new IllegalArgumentException("Список задач в порядке приоритета пуст: ничего не удалено!");
        }
    }

    private void resolveStartTime(Epic epic) {
        boolean noSubtasks = epic.getSubTaskIds().isEmpty();
        Instant startTime = Instant.ofEpochMilli(0);
        if (noSubtasks) {
            epic.setStartTime(startTime);
        } else {
            for (SubTask subTask : getEpicSubTasksByEpicId(epic.getId())) {
                Instant startTimeTemp = subTask.getStartTime();
                if (startTime == null) {
                    startTime = startTimeTemp;
                } else {
                    if (startTime.isAfter(startTimeTemp)) {
                        startTime = startTimeTemp;
                    }
                }
            }
        }
        epic.setStartTime(startTime);
    }

    private void resolveDurations(Epic epic) {
        boolean noSubtasks = epic.getSubTaskIds().isEmpty();
        Duration duration = Duration.ZERO;
        if (noSubtasks) {
            epic.setDuration(Duration.ofMinutes(0));
        } else {
            for (SubTask subTask : getEpicSubTasksByEpicId(epic.getId())) {
                duration.plus(subTask.getDuration());
            }
        }
        epic.setDuration(duration);
    }
}


