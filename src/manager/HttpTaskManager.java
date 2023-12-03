package manager;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import manager.*;

import server.KVTaskClient;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private  Gson gson = GsonUtils.getInstance();
    private final KVTaskClient taskClient;
    public HttpTaskManager(URI uri) throws ManagerSaveException {
        super(null);
        try {
            taskClient = new KVTaskClient(uri);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при подключении к KVServer");
        }
    }

    public void load() {
        try {
            Map<Integer, Task> tasks = gson.fromJson(
                    taskClient.load("tasks"),
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType()
            );
            Map<Integer, Epic> epics = gson.fromJson(
                    taskClient.load("epics"),
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType()
            );
            Map<Integer, SubTask> subtasks = gson.fromJson(
                    taskClient.load("subtasks"),
                    new TypeToken<HashMap<Integer, SubTask>>() {
                    }.getType()
            );
            List<Task> historyList = gson.fromJson(
                    taskClient.load("history"),
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            HistoryManager history = new InMemoryHistoryManager();
            historyList.forEach(history::add);

            int startId = Integer.parseInt(taskClient.load("startId"));

            this.tasks = tasks;
            this.epics = epics;
            this.subTasks = subtasks;
            this.historyManager = history;
            this.prioritizedTaskSet.addAll(tasks.values());
            this.prioritizedTaskSet.addAll(epics.values());
            this.prioritizedTaskSet.addAll(subtasks.values());
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при восстановлении данных");
        }
    }

    @Override
    protected void save() throws ManagerSaveException {
        try {
            taskClient.put("tasks", gson.toJson(tasks));
            taskClient.put("epics", gson.toJson(epics));
            taskClient.put("subtasks", gson.toJson(subTasks));
            taskClient.put("history", gson.toJson(historyManager.getHistory()));
        } catch (IOException | InterruptedException err) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }
}

