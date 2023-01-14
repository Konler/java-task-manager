package manager;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Task;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public static final Path path = Path.of("file.CSV");
    File file;

    @BeforeEach
    public void beforeEach() {
        file = new File(String.valueOf(path));
        manager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(file.toPath());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() throws ManagerSaveException {
        Task task = new Task("Task1", "Description1", Status.NEW, Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
        manager.createTask(task);
        Epic epic = new Epic("Epic1", "Decription1", Status.NEW, Instant.ofEpochMilli(987654456l), Duration.ofMinutes(7));
        manager.createEpic(epic);
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() throws ManagerSaveException {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() throws ManagerSaveException {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void souldTrowExcepyionWhenLoadFromFile() {
        manager = new FileBackedTasksManager(file);
        manager.deleteAllSubTasks();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        ManagerSaveException exception = Assertions.assertThrows(
                ManagerSaveException.class,
                () -> {
                    File file = new File("file.CSV");
                    FileBackedTasksManager fromFile = FileBackedTasksManager.loadFromFile(file);
                });
        assertEquals("Файл не найден", exception.getMessage());
    }
}