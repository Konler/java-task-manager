import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private Integer idGenerator = 0;

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }


    public void printTasks(HashMap<Integer, Task> tasks) {
        for (Task task : tasks.values()) {
            System.out.println("Имя задачи: " + task.name + "; Id задачи: " + task.getId() + "; Описание задачи: "
                    + task.description + "; Статус задачи: " + task.status);
        }
    }

    public void printSubTasks(HashMap<Integer, Subtask> subtasks) {
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Имя подзадачи: " + subtask.name + "; Id подзадачи: " + subtask.getId()
                    + "; Описание подзадачи: " + subtask.description + "; Статус подзадачи: " + subtask.status);
        }
    }

    public void printEpiks(HashMap<Integer, Epic> epics) {
        for (Epic epic : epics.values()) {
            System.out.println("Имя эпика: " + epic.name + "; Id эпика: " + epic.getId()
                    + "; Описание эпика: " + epic.description + "; Статус эпика: " + epic.getStatus());
        }
    }

    private Integer getNextId() {
        return idGenerator++;
    }

    ArrayList<Task> getAllTasks() {
        return (ArrayList<Task>) tasks.values();
    }

    ArrayList<Subtask> getAllSubTasks() {
        return (ArrayList<Subtask>) subTasks.values();
    }

    ArrayList<Epic> getAllEpics() {
        return (ArrayList<Epic>) epics.values();
    }

    void deleteAllTask() {
        tasks.clear();
    }

    void deleteAllSubtasks() {
        subTasks.clear();
    }

    void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    Task getTaskById(Integer taskId) {
        return tasks.get(taskId);
    }

    Subtask getSubtaskById(Integer subtaskId) {
        if (subTasks.containsKey(subtaskId)) {
            return subTasks.get(subtaskId);
        } else {
            System.out.println("Нет такого subtask");
            return null;
        }
    }

    Epic getEpicById(Integer idEpic) {
        if (epics.containsKey(idEpic)) {
            return epics.get(idEpic);
        } else {
            System.out.println("Нет такого epic");
            return null;
        }
    }

    void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);

    }

    void createSubTask(Subtask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            epics.get(subTask.getEpicId()).getSubTasks().add(subTask.getId());
            subTask.setId(getNextId());
            subTasks.put(subTask.getId(), subTask);
            resolveEpicNewStatus(epics.get(subTask.getEpicId()));
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTasks().add(subTask.getId());
        } else {
            System.out.println("Подзадача не может быть создана без epic задачи");
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Task с указанным id не существует");
        }
    }

    public void updateSubTask(Subtask subtask) {
        if (subTasks.containsKey((subtask.getId()))) {
            subTasks.put(subtask.getId(), subtask);
            resolveEpicNewStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Subtask с указанным Id не существует");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey((epic.getId()))) {
            epics.put(epic.getId(), epic);
            resolveEpicNewStatus(epic);
        } else {
            System.out.println("Epic с указанным Id не существует");
        }
    }


    void deleteTaskById(Integer id) {
        tasks.remove(id);
    }


    void deleteAllSubtaskByEpicId(Integer idEpic) {
        Epic epic = epics.get(idEpic);
        epic.getSubTasks().clear();
        resolveEpicNewStatus(epic);
    }

    void deleteEpicById(Integer idEpic) {
        deleteAllSubtaskByEpicId(idEpic);
        epics.remove(idEpic);

    }

    void deleteSubtaskById(Integer subtaskId) {
        Subtask subtask = getSubtaskById(subtaskId);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.deleteSubtaskByIdFromEpic(subtaskId);
        subTasks.remove(subtaskId);
        resolveEpicNewStatus(epic);
    }

    private void resolveEpicNewStatus(Epic epic) {
        epics.get(epic.getId()).setStatus(null);
        if (epics.containsKey(epic.getId())) {
            int countDone = 0;
            int countNew = 0;
            for (Subtask subtask : subTasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    switch (subtask.status) {
                        case "DONE":
                            countDone++;
                            break;
                        case "NEW":
                            countNew++;
                            break;
                        case "IN_PROGRESS":
                            return;
                    }
                    if (countDone == epic.getSubTasks().size()) {
                        epic.setStatus("DONE");
                    } else if (countNew == epic.getSubTasks().size()) {
                        epic.setStatus("NEW");
                    } else {
                        epic.setStatus("IN_PROGRESS");
                    }
                }
            }
        } else {
            System.out.println("Не является epic задачей");
        }
    }

    ArrayList<Subtask> getLisOfSubtaskOfEpic(Epic epic) {
        ArrayList<Integer> subtasksIdsList = epic.getSubTasks();
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (int i = 0; i < subtasksIdsList.size(); i++) {
            if (subTasks.containsKey(subtasksIdsList.get(i))) {
                subtasksList.add(subTasks.get(subtasksIdsList.get(i)));
            } else {
                System.out.println("Эпик не имеет подзадач");
                break;
            }
        }
        return subtasksList;
    }
}
