package manager;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

class HttpTaskServerTest {
    private static final String uriBase = "http://localhost:8080";
    private static HttpClient httpClient;
    private static Gson gson;
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static final int LOOPS = 3;

    HttpTaskServerTest() {
    }

    @BeforeAll
    static void initBeforeAll() throws IOException {

        gson = new Gson();
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3L)).build();
        taskServer = new HttpTaskServer();
        taskServer.startServer();
        manager = new InMemoryTaskManager();


    }

    @AfterAll
    static void releaseAfterAll() {
        try {
            taskServer.stopServer();
        } catch (Throwable var1) {
            throw var1;
        }
    }

    @BeforeEach
    void initBeforeEach() {
        manager.deleteAllTasks();
    }

    @Test
    void testStartAndStopServer() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                taskServer.stopServer();
            });
            Assertions.assertDoesNotThrow(() -> {
                taskServer.startServer();
            });
        } catch (Throwable var2) {
            throw var2;
        }
    }

    @Test
    void testRootHandlers() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/")).version(Version.HTTP_1_1).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotEquals(0, ((String) response.body()).length());

    }

    @Test
    void testTasksHandlers() throws IOException, InterruptedException, ManagerSaveException {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/tasks/")).version(Version.HTTP_1_1).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listType = (new TypeToken<ArrayList<Task>>() {
        }).getType();
        List<Task> tasks = (List) gson.fromJson((String) response.body(), listType);
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(manager.getTasks().size(), tasks.size());
        String lastResponse = null;

        Task task;
        for (int i = 1; i <= 3; ++i) {
            task = new Task("Task1", "Description1", Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
            manager.createTask(task);
            task.setStatus(Status.values()[i % Status.values().length]);
            Epic epic = new Epic("Epic#" + i, "Epic descr #" + i);
            manager.createEpic(epic);

            for (int j = 1; j <= 3; ++j) {
                SubTask subTask = new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epic.getId());
                manager.createSubTask(subTask);
            }

            response = httpClient.send(request, BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            lastResponse = (String) response.body();
            listType = (new TypeToken<ArrayList<Task>>() {
            }).getType();
            tasks = (List) gson.fromJson((String) response.body(), listType);
            Assertions.assertNotNull(tasks);
            Assertions.assertEquals(manager.getTasks().size(), tasks.size());
        }

        Iterator var12 = manager.getAllTasks().iterator();

        while (var12.hasNext()) {
            task = (Task) var12.next();
            String s = gson.toJson(task);
            Assertions.assertTrue(lastResponse.contains(s));
        }


    }

    @Test
    void testHistoryHandlers() throws IOException, InterruptedException, ManagerSaveException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/tasks/history/")).version(Version.HTTP_1_1).build();
        for (int i = 1; i <= 3; ++i) {
            Task task = new Task("Task1", "Description1", Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5));
            manager.createTask(task);
            manager.getTaskById(task.getId());
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            String lastResponse = (String) response.body();
            Type listType = (new TypeToken<ArrayList<Task>>() {
            }).getType();
            List<Task> history = (List) gson.fromJson((String) response.body(), listType);
            Assertions.assertNotNull(history);
            Assertions.assertEquals(manager.getTasks(), history.size());
            String s = gson.toJson(task);
            Assertions.assertTrue(lastResponse.contains(s));
        }
    }

    @Test
    void testTaskHandlers() throws IOException, ManagerSaveException, InterruptedException {

        Task[] taskOrig = new Task[]{new Task("Task1", "Description1", Instant.ofEpochMilli(4567890l), Duration.ofMinutes(5))};
        manager.createTask(taskOrig[0]);
        Task taskCopy = new Task("", "");
        taskCopy.copyFrom(taskOrig[0]);
        Integer[] taskId = {null};
        HttpClient var10000 = httpClient;
        Builder var10001 = HttpRequest.newBuilder().PUT(BodyPublishers.ofString(""));
        int var10002 = taskOrig[0].getId();
        HttpResponse<String> response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/task/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().GET();
        var10002 = taskOrig[0].getId();
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/task/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/tasks/task/" + taskOrig[0].getId())).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(taskOrig[0]), response.body());
        String responseStr1 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            taskOrig[0] = (Task) gson.fromJson(responseStr1, taskOrig[0].getClass());
        });
        taskOrig[0].setName(taskOrig[0].getName() + "+++");
        String json = gson.toJson(taskOrig[0]);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/task/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        String responseStr2 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            taskId[0] = (Integer) gson.fromJson(responseStr2, Integer.class);
        });
        Assertions.assertEquals(taskOrig[0].getId(), taskId[0]);
        taskCopy.setStartTime(taskCopy.getStartTime().plusSeconds(100000L));
        json = gson.toJson(taskCopy);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/task/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), (String) response.body());
        String responseStr3 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            taskId[0] = (Integer) gson.fromJson(responseStr3, Integer.class);
        });
        Assertions.assertNotNull(manager.getTaskById(taskId[0]));
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().DELETE();
        var10002 = taskId[0];
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/task/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().DELETE().uri(URI.create("http://localhost:8080/tasks/task/" + taskId[0])).build(), BodyHandlers.ofString());
        Assertions.assertEquals(202, response.statusCode());
        Assertions.assertNull(manager.getTaskById(taskId[0]));

    }

    @Test
    void testEpicHandlers() throws IOException, ManagerSaveException, InterruptedException {
        Epic[] epicOrig = new Epic[]{new Epic("EpicName", "Epic description")};
        manager.createTask(epicOrig[0]);
        Epic epicCopy = new Epic("", "");
        epicCopy.copyFrom(epicOrig[0]);
        Integer[] epicId = {null};
        HttpClient var10000 = httpClient;
        Builder var10001 = HttpRequest.newBuilder().PUT(BodyPublishers.ofString(""));
        int var10002 = epicOrig[0].getId();
        HttpResponse<String> response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/epic/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().GET();
        var10002 = epicOrig[0].getId();
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/epic/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/tasks/epic/" + epicOrig[0].getId())).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(epicOrig[0]), response.body());
        String responseStr1 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            epicOrig[0] = (Epic) gson.fromJson(responseStr1, epicOrig[0].getClass());
        });
        epicOrig[0].setName(epicOrig[0].getName() + "+++");
        String json = gson.toJson(epicOrig[0]);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/epic/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        String responseStr2 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            epicId[0] = (Integer) gson.fromJson(responseStr2, Integer.class);
        });
        Assertions.assertEquals(epicOrig[0].getId(), epicId[0]);
        json = gson.toJson(epicCopy);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/epic/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), (String) response.body());
        String responseStr3 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            epicId[0] = (Integer) gson.fromJson(responseStr3, Integer.class);
        });
        Assertions.assertNotNull(manager.getTaskById(epicId[0]));
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().DELETE();
        var10002 = epicId[0];
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/epic/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().DELETE().uri(URI.create("http://localhost:8080/tasks/epic/" + epicId[0])).build(), BodyHandlers.ofString());
        Assertions.assertEquals(202, response.statusCode());
        Assertions.assertNull(manager.getTaskById(epicId[0]));
    }

    @Test
    void testSubtaskHandlers() throws IOException, ManagerSaveException, InterruptedException {
        Epic epicOrig = new Epic("EpicName 1", "Epic description 1");
        Epic epicCopy = new Epic("EpicName2", "Epic description 2");
        manager.createTask(epicOrig);
        SubTask[] subTaskOrig = new SubTask[]{new SubTask("SubTask5", "Description", Status.NEW, Instant.ofEpochMilli(567890l), Duration.ofMinutes(15), epicOrig.getId())};
        manager.createTask(subTaskOrig[0]);
        SubTask subTaskCopy = new SubTask("", "", epicCopy.getId());
        subTaskCopy.copyFrom(subTaskOrig[0]);
        Integer[] subTaskId = new Integer[]{null};
        HttpClient var10000 = httpClient;
        Builder var10001 = HttpRequest.newBuilder().PUT(BodyPublishers.ofString(""));
        int var10002 = subTaskOrig[0].getId();
        HttpResponse<String> response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/subtask/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().GET();
        var10002 = subTaskOrig[0].getId();
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/subtask/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/tasks/subtask/" + subTaskOrig[0].getId())).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(subTaskOrig[0]), response.body());
        String responseStr1 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            subTaskOrig[0] = (SubTask) gson.fromJson(responseStr1, subTaskOrig[0].getClass());
        });
        subTaskOrig[0].setName(subTaskOrig[0].getName() + "+++");
        String json = gson.toJson(subTaskOrig[0]);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/subtask/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        String responseStr2 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            subTaskId[0] = (Integer) gson.fromJson(responseStr2, Integer.class);
        });
        Assertions.assertEquals(subTaskOrig[0].getId(), subTaskId[0]);
        subTaskCopy.setStartTime(subTaskCopy.getStartTime().plusSeconds(10000L));
        json = gson.toJson(subTaskCopy);
        response = httpClient.send(HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create("http://localhost:8080/tasks/subtask/")).build(), BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), (String) response.body());
        String responseStr3 = (String) response.body();
        Assertions.assertDoesNotThrow(() -> {
            subTaskId[0] = (Integer) gson.fromJson(responseStr3, Integer.class);
        });
        Assertions.assertNotNull(manager.getTaskById(subTaskId[0]));
        var10000 = httpClient;
        var10001 = HttpRequest.newBuilder().DELETE();
        var10002 = subTaskId[0];
        response = var10000.send(var10001.uri(URI.create("http://localhost:8080/tasks/subtask/" + (var10002 + 10000))).build(), BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        response = httpClient.send(HttpRequest.newBuilder().DELETE().uri(URI.create("http://localhost:8080/tasks/subtask/" + subTaskId[0])).build(), BodyHandlers.ofString());
        Assertions.assertEquals(202, response.statusCode());
        Assertions.assertNull(manager.getTaskById(subTaskId[0]));
    }
}
