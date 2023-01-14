package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public T manager;

    @Test
    void shouldCheckCrossingTasks() throws IOException, ManagerSaveException {
        Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
        manager.createTask(task);
        Task task1=new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567992l), Duration.ofMinutes(5));
        ManagerSaveException exception = assertThrows(

                ManagerSaveException.class, () ->{ manager.createTask(task1);
    });

assertEquals("Пересечение задач!"+task1.getId()+"не сохранена",exception.getMessage());
}



    @Test
    public void shouldReturnHistoryWithTasks() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subTask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subTask));
        assertTrue(list.contains(epic));
    }

    @Test
    void getHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        assertEquals(inMemoryHistoryManager.getTasks(), historyManager.getHistory());
        assertNotNull(historyManager.getHistory(), "История не пуста");
    }

    @Test
    void getAllTasks() {
        assertEquals(new ArrayList(manager.getTasks().values()), manager.getAllTasks());
        assertNotNull(manager.getAllTasks(), "Лист Task не пустой");
    }

    @Test
    void getAllSubTasks() {
        assertEquals(new ArrayList(manager.getSubTasks().values()), manager.getAllSubTasks());
        assertNotNull(manager.getAllSubTasks(), "Лист SubTask не пустой");
    }

    @Test
    void getAllEpics() {
        assertEquals(new ArrayList(manager.getEpics().values()), manager.getAllEpics());
        assertNotNull(manager.getAllEpics(), "Лист Epics не пустой");
    }

    @Test
    void deleteAllTasks() {
        HashMap<Integer, Task> hashTask = manager.getTasks();
        hashTask.clear();
        manager.deleteAllTasks();
        assertEquals(hashTask, manager.getTasks());
        assertNotNull(manager.getTasks());
    }

    @Test
    void deleteAllSubTasks() {
        HashMap<Integer, SubTask> hashTask = manager.getSubTasks();
        hashTask.clear();
        manager.deleteAllSubTasks();
        assertEquals(hashTask, manager.getSubTasks());
        assertNotNull(manager.getSubTasks());
    }

    @Test
    void deleteAllEpics() {
        HashMap<Integer, Epic> hashTask = manager.getEpics();
        hashTask.clear();
        manager.deleteAllEpics();
        assertEquals(hashTask, manager.getEpics());
        assertNotNull(manager.getEpics());
    }

    @Test
    void getTaskById() throws IOException, ManagerSaveException {
        Task task = new Task("Task5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createTask(task);
        assertEquals(task, manager.getTaskById(task.getId()));
        assertNotNull(manager.getTaskById(task.getId()));
    }

    @Test
    void getSubTaskById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));
        assertNotNull(manager.getSubTaskById(subTask.getId()));
    }

    @Test
    void getEpicById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertNotNull(manager.getEpicById(epic.getId()));
    }

    @Test
    void createTask() throws IOException, ManagerSaveException {
        Task task = new Task("Task5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createTask(task);
        assertEquals(task, manager.getTaskById(task.getId()));
        assertNotNull(manager.getTaskById(task.getId()));
        List<Task> tasks = manager.getAllTasks();
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    void createEpic() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertNotNull(manager.getEpicById(epic.getId()));
        List<Epic> epics = manager.getAllEpics();
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubTaskIds());
        assertEquals(List.of(epic), epics);
    }

    @Test
    void createSubTask() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));
        assertNotNull(manager.getSubTaskById(subTask.getId()));
        List<SubTask> subtasks = manager.getAllSubTasks();
        assertNotNull(subTask.getStatus());
        assertEquals(epic.getId(), subTask.getEpicId());
        assertEquals(Status.NEW, subTask.getStatus());
        assertEquals(List.of(subTask), subtasks);
        assertEquals(List.of(subTask.getId()), epic.getSubTaskIds());

    }

    @Test
    void createSubTaskNullPoint() {
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), 10);
        assertThrows(NullPointerException.class, () -> {
            manager.getSubTaskById(subTask.getId());
        });
    }

    @Test
    void updateTask() throws IOException, ManagerSaveException {
        Task task = new Task("Task5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createTask(task);
        task.setDescription("Des");
        manager.updateTask(task);
        assertEquals(task, manager.getTaskById(task.getId()));
        assertNotNull(manager.getTaskById(task.getId()));
    }

    @Test
    void updateSubTask() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        subTask.setDescription("Des");
        manager.updateTask(subTask);
        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));
        assertNotNull(manager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void shouldUpdateTaskStatus() throws IOException, ManagerSaveException {
        Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void updateSubTask2() {
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), 10);
        assertThrows(Exception.class, () -> {
            manager.updateSubTask(subTask);
        });
    }

    @Test
    void updateEpic() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        epic.setDescription("Des");
        manager.updateEpic(epic);
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertNotNull(manager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertEquals(Status.IN_PROGRESS, manager.getSubTaskById(subTask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() throws IOException, ManagerSaveException {
        Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void deleteTaskById() throws IOException, ManagerSaveException {
        Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
        manager.createTask(task);
        Task task2 = new Task("Task2", "Description2", Status.NEW, Instant.ofEpochMilli(45678905678l), Duration.ofMinutes(15));
        manager.createTask(task2);
        HashMap<Integer, Task> hash = new HashMap<>();
        hash.put(task2.getId(), task2);
        manager.deleteTaskById(task.getId());
        assertEquals(hash, manager.getTasks());
        assertNotNull(manager.getTasks());
    }

    @Test
    void deleteEpicById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        HashMap<Integer, Epic> hash = new HashMap<>();
        hash.put(epic.getId(), epic);
        assertEquals(hash, manager.getEpics());
        assertNotNull(manager.getEpics());
    }

    @Test
    void deleteSubTaskById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask);
        HashMap<Integer, SubTask> hash = new HashMap<>();
        hash.put(subTask.getId(), subTask);
        assertEquals(hash, manager.getSubTasks());
        assertNotNull(manager.getSubTasks());
    }

    @Test
    void getEpicSubTasksByEpicId() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic5", "Description", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(15));
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask1", "Description1", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(2), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", Status.IN_PROGRESS, Instant.ofEpochMilli(5678799990l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask2);
        ArrayList<SubTask> arrayList = new ArrayList<>();
        arrayList.add(subTask);
        arrayList.add(subTask2);
        assertEquals(arrayList, manager.getEpicSubTasksByEpicId(epic.getId()));
        assertNotNull(manager.getEpicSubTasksByEpicId(epic.getId()));
    }

    @Test
//   a.   Пустой список подзадач.
    void shouldStatusNewIfNoSubtasksresolveEpicNewStatus() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        manager.resolveEpicNewStatus(epic);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        assertNotNull(manager.getEpics().get(epic.getId()).getStatus());
    }

    @Test
//b.   Все подзадачи со статусом NEW.
    void shouldStatusNewIfAllSubtasksNewresolveEpicNewStatus() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        manager.resolveEpicNewStatus(epic);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        SubTask subTask = new SubTask("SubTask1", "Description1", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(2), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", Status.NEW, Instant.ofEpochMilli(5678799990l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask2);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        assertNotNull(manager.getEpics().get(epic.getId()).getStatus());
    }


    @Test
//Все подзадачи со статусом DONE.
    void shouldStatusDoneIfAllSubtasksDoneResolveEpicNewStatus() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        manager.resolveEpicNewStatus(epic);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        SubTask subTask = new SubTask("SubTask1", "Description1", Status.DONE, Instant.ofEpochMilli(567890l), Duration.ofMinutes(2), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", Status.DONE, Instant.ofEpochMilli(5678799990l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask2);
        assertEquals(Status.DONE, manager.getEpics().get(epic.getId()).getStatus());
        assertNotNull(manager.getEpics().get(epic.getId()).getStatus());
    }

    @Test
//d.    Подзадачи со статусами NEW и DONE.
    void shouldStatusNewIfAllSubtasksNewrAndDoneResolveEpicNewStatus() throws IOException, ManagerSaveException {
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        manager.resolveEpicNewStatus(epic);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        SubTask subTask = new SubTask("SubTask1", "Description1", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(2), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", Status.DONE, Instant.ofEpochMilli(5678799990l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, manager.getEpics().get(epic.getId()).getStatus());
        assertNotNull(manager.getEpics().get(epic.getId()).getStatus());
    }

    @Test
// e.    Подзадачи со статусом IN_PROGRESS.
    void shouldStatusInProgressIfAllSubtasksInProgressResolveEpicNewStatus() throws ManagerSaveException, IOException {
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        manager.resolveEpicNewStatus(epic);
        assertEquals(Status.NEW, manager.getEpics().get(epic.getId()).getStatus());
        SubTask subTask = new SubTask("SubTask1", "Description1", Status.IN_PROGRESS, Instant.ofEpochMilli(567890l), Duration.ofMinutes(2), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", Status.IN_PROGRESS, Instant.ofEpochMilli(5678799990l), Duration.ofMinutes(15), epic.getId());
        manager.createSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, manager.getEpics().get(epic.getId()).getStatus());
        assertNotNull(manager.getEpics().get(epic.getId()).getStatus());
    }

}