package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    File file;
    HashMap<Integer, Task> tasks = super.tasks;
    private final HashMap<Integer, SubTask> subTasks = super.subTasks;
    private final HashMap<Integer, Epic> epics = super.epics;
    private final HistoryManager historyManager = super.historyManager;

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public FileBackedTasksManager(File file) {
        this.file = file;

    }

    public static void main(String[] args) throws Exception {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        File file = new File("file.CSV");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        Task task1 = new Task("Task1", "Description task1", Status.NEW);
        fileBackedTasksManager.createTask(task1);

        Task task2 = new Task("Task2", "Description", Status.IN_PROGRESS);
        fileBackedTasksManager.createTask(task2);
        
        Epic epic1 = new Epic("Epic1", "Description", Status.NEW);
        fileBackedTasksManager.createEpic(epic1);

        SubTask subTask1 =new SubTask("SubTask1","Description", 2,Status.NEW);
        fileBackedTasksManager.createSubTask(subTask1);

        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getTaskById(task2.getId());
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getEpicById(epic1.getId());
        fileBackedTasksManager.getSubTaskById(subTask1.getId());

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
    }

   private void save() throws IOException, ManagerSaveException {//метод будет сохранять текущее состояние менеджера в указанный файл
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
                throw new ManagerSaveException("Исключение ManagerSaveException");
        }

    }


    @Override
    public List<Task> getHistory() {
        return null;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return null;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return null;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return null;
    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteAllSubTasks() {

    }

    @Override
    public void deleteAllEpics() {

    }

    @Override
    public Task getTaskById(Integer taskId) throws IOException, ManagerSaveException {
        super.getTaskById(taskId);
        save();
        return tasks.get(taskId);
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) throws IOException, ManagerSaveException {
        super.getSubTaskById(subTaskId);
        save();
        return subTasks.get(subTaskId);
    }

    @Override
    public Epic getEpicById(Integer epicId) throws IOException, ManagerSaveException {
        super.getEpicById(epicId);
        save();
        return epics.get(epicId);
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException, IOException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) throws IOException, ManagerSaveException {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) throws IOException, ManagerSaveException {
        super.createSubTask(subTask);
        save();
    }


    @Override
    public ArrayList<SubTask> getEpicSubTasksByEpicId(Integer epicId) {
        return super.getEpicSubTasksByEpicId(epicId);
    }


    static String toString(Task task) {
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        String type = null;
        String string = null;
        if (task instanceof SubTask) {
            type = "SUBTASK";
            SubTask subTask = (SubTask) task;
            String epicId = subTask.getEpicId().toString();
            string = id + ", " + type + ", " + name + ", " + status + ", " + description + ", " + epicId + "\n";
        } else if (task instanceof Epic) {
            type = "EPIC";
            string = id + ", " + type + ", " + name + ", " + status + ", " + description + "\n";
        } else {
            type = "TASK";
            string = id + ", " + type + ", " + name + ", " + status + ", " + description + "\n";
        }
        return string;
    }

    static Task fromString(String value) {
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String name = elem[2];
        String description = elem[4];
        Status status = Status.valueOf(elem[3].trim());
        int epicNumber = 0;
        if (elem.length == 6) {
            epicNumber = Integer.parseInt(elem[5].trim());
        }
        if ("EPIC".equals(elem[1].trim())) {
            Epic epic = new Epic(name, description, status);
            epic.setId(id);
            epic.setStatus(Status.valueOf(elem[3].trim().toUpperCase()));
            return epic;
        } else if ("SUBTASK".equals(elem[1].trim())) {
            SubTask subTask = new SubTask(name, description, epicNumber, status);
            subTask.setId(id);
            return subTask;
        } else {
            Task task = new Task(name, description, status);
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

    static List<Integer> historyFromString(String value) {                                     //Восстановление списка id из CVS  для менеджера истории
        List<Integer> history = new ArrayList<>();
        String[] val = value.split(",");
        for (String string : val) {
            history.add(Integer.parseInt(string));
        }
        return history;
    }


    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
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
                        fileBackedTasksManager.epics.put(newTaskId,newTask);
                        System.out.println(FileBackedTasksManager.toString(fileBackedTasksManager.epics.get(newTaskId)));
                    } else if (lineElements[1].equals("SUBTASK")) {
                        SubTask newTask = (SubTask) fromString(line);
                        int newTaskId = newTask.getId();
                        fileBackedTasksManager.subTasks.put(newTaskId,newTask);
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
