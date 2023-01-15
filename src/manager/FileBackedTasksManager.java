package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    File file;

    public FileBackedTasksManager(File file) {
        this.file = file;

    }

    void save() throws ManagerSaveException {//метод будет сохранять текущее состояние менеджера в указанный файл
        String s = file.getName();//получить имя файла
        try (FileWriter fileWriter = new FileWriter(s)) {
            if (file.length() == 0) {
                fileWriter.write("id,type,name,status,description,epic\n");
            }
            for (Task task : tasks.values()) {
                String string = toString(task);
                fileWriter.write(string);
            }
            for (Task task : subTasks.values()) {
                String string = toString(task);
                fileWriter.write(string);
            }
            for (Task task : epics.values()) {
                String string = toString(task);
                fileWriter.write(string);
            }
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Файл не найден");
        }
    }

    @Override
    public Task getTaskById(Integer taskId) throws ManagerSaveException {
        super.getTaskById(taskId);
        save();
        return tasks.get(taskId);
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) throws ManagerSaveException {
        super.getSubTaskById(subTaskId);
        save();
        return subTasks.get(subTaskId);
    }

    @Override
    public Epic getEpicById(Integer epicId) throws ManagerSaveException {
        super.getEpicById(epicId);
        save();
        return epics.get(epicId);
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        super.getTaskById(task.getId());
        save();
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        super.getEpicById(epic.getId());
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) throws ManagerSaveException {
        super.createSubTask(subTask);
        super.getSubTaskById(subTask.getId());
        save();
    }

    static String toString(Task task) {
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        Instant startTime = task.getStartTime();
        Duration duration = task.getDuration();
        String type = null;
        StringBuilder string = new StringBuilder();
        if (task instanceof SubTask) {
            type = "SUBTASK";
            SubTask subTask = (SubTask) task;
            String epicId = subTask.getEpicId().toString();
            string.append(id).append(", ").append(type).append(", ").append(name).append(", ").append(status).append(", ").append(description).append(", ").append(startTime).append(", ").append(duration).append(", ").append(epicId).append("\n");

        } else if (task instanceof Epic) {
            type = "EPIC";
            string.append(id).append(", ").append(type).append(", ").append(name).append(", ").append(status).append(", ").append(description).append(", ").append(startTime).append(", ").append(duration).append("\n");

        } else {
            type = "TASK";
            string.append(id).append(", ").append(type).append(", ").append(name).append(", ").append(status).append(", ").append(description).append(", ").append(startTime).append(", ").append(duration).append("\n");

        }
        return string.toString();
    }

    static Task fromString(String value) {
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String name = elem[2];
        String description = elem[4];
        Status status = Status.valueOf(elem[3].trim());
        String stringStart = elem[5].trim();
        Instant startTime = Instant.parse(stringStart);
        Duration duration = Duration.ZERO;
        if (elem[6].endsWith("M")) {
            String subTring = elem[6].substring(elem[6].indexOf('T') + 1, elem[6].indexOf('M'));
            duration = Duration.ofMinutes(Long.parseLong(subTring));
        } else {
            String subTring = elem[6].substring(elem[6].indexOf('T') + 1, elem[6].indexOf('S'));
            duration = Duration.ofMinutes(Long.parseLong(subTring));
        }
        int epicNumber = 0;
        if (elem.length == 8) {
            epicNumber = Integer.parseInt(elem[7].trim());
        }
        if ("EPIC".equals(elem[1].trim())) {
            Epic epic = new Epic(name, description, status, startTime, duration);
            epic.setId(id);
            epic.setStatus(Status.valueOf(elem[3].trim().toUpperCase()));
            return epic;
        } else if ("SUBTASK".equals(elem[1].trim())) {
            SubTask subTask = new SubTask(name, description, status, startTime, duration, epicNumber);
            subTask.setId(id);

            return subTask;
        } else {
            Task task = new Task(name, description, status, startTime, duration);
            task.setId(id);
            return task;
        }
    }

    static String historyToString(HistoryManager manager) { // готовим строку для сохранения менеджера истории в CVS
        // System.out.println("\n");
        List<Task> historyTask = manager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < historyTask.size(); i++) {
            builder.append(historyTask.get(i).getId());//Последовательно добавляем Id задач
            if (i != historyTask.size() - 1) {
                builder.append(",");
            }
        }
        String str = "\n" + builder.toString();
        return str;
    }

    static List<Integer> historyFromString(String value) {       //Восстановление списка id из CVS  для менеджера истории
        List<Integer> history = new ArrayList<>();
        if (history.isEmpty()) {
            return history;
        }
        String[] val = value.split(",");
        for (String string : val) {
            history.add(Integer.parseInt(string));
        }
        return history;
    }

    static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        List<Integer> historyArr = new ArrayList<>();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.startsWith("i")) {
                    String lineId = line;
                } else if (line.isBlank()) {
                    line = fileReader.readLine();
                    historyArr = historyFromString(line);
                } else {
                    String[] lineElements = line.split(", ");
                    if (lineElements[1].equals("EPIC")) {
                        Epic newTask = (Epic) fromString(line);
                        int newTaskId = newTask.getId();
                        fileBackedTasksManager.epics.put(newTaskId, newTask);
                        System.out.println(FileBackedTasksManager.toString(fileBackedTasksManager.epics.get(newTaskId)));
                    } else if (lineElements[1].equals("SUBTASK")) {
                        SubTask newTask = (SubTask) fromString(line);
                        int newTaskId = newTask.getId();
                        fileBackedTasksManager.subTasks.put(newTaskId, newTask);
                        System.out.println(FileBackedTasksManager.toString(fileBackedTasksManager.subTasks.get(newTaskId)));
                    } else {
                        Task newTask = fromString(line);
                        int newTaskId = newTask.getId();
                        fileBackedTasksManager.tasks.put(newTaskId, newTask);
                        System.out.println(FileBackedTasksManager.toString(fileBackedTasksManager.tasks.get(newTaskId)));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл не найден");
        } catch (IIOException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(historyArr);
        return fileBackedTasksManager;
    }
}
