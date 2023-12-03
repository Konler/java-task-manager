package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    public static final String ROOT_PATH = "/";
    public static final String TASKS_PATH = "/tasks/";
    public static final String TASKS_TASK_PATH = "/tasks/task/";
    public static final String TASKS_EPIC_PATH = "/tasks/epic/";
    public static final String TASKS_SUBTASK_PATH = "/tasks/subtask/";
    public static final String TASKS_HISTORY_PATH = "/tasks/history/";
    private static final Charset DEFAULT_CHARSET;
    private static final Map<String, HttpHandler> handlers;
    private HttpServer httpServer;

    public HttpTaskServer() {
    }

    private static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0L);
            OutputStream os = exchange.getResponseBody();
            try {
                os.write(String.valueOf(responseCode).getBytes(DEFAULT_CHARSET));
            } catch (Throwable var10) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable var8) {
                        var10.addSuppressed(var8);
                    }
                }
                throw var10;
            }
            if (os != null) {
                os.close();
            }
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, (long) bytes.length);
            OutputStream os = exchange.getResponseBody();
            try {
                os.write(bytes);
            } catch (Throwable var9) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable var7) {
                        var9.addSuppressed(var7);
                    }
                }
                throw var9;
            }
            if (os != null) {
                os.close();
            }
        }
        exchange.close();
    }

    public void startServer() throws IOException {
        this.httpServer = HttpServer.create();
        Managers.getDefault();
        this.httpServer.bind(new InetSocketAddress(8080), 0);
        Iterator var1 = handlers.entrySet().iterator();

        while (var1.hasNext()) {
            Entry<String, HttpHandler> handlerEntry = (Entry) var1.next();
            this.httpServer.createContext((String) handlerEntry.getKey(), (HttpHandler) handlerEntry.getValue());
        }

        this.httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
    }

    public void stopServer() {
        this.httpServer.stop(3);
        System.out.println("HTTP-сервер остановлен!");
    }

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        handlers = new LinkedHashMap();
        handlers.put("/", new HttpTaskServer.RootHandler());
        handlers.put("/tasks/", new HttpTaskServer.TasksHandler());
        handlers.put("/tasks/task/", new HttpTaskServer.TaskHandler(Task.class, Task.class));
        handlers.put("/tasks/epic/", new HttpTaskServer.TaskHandler(Epic.class, Task.class));
        handlers.put("/tasks/subtask/", new HttpTaskServer.TaskHandler(SubTask.class, SubTask.class));
        handlers.put("/tasks/history/", new HttpTaskServer.HistoryHandler());
    }

    static class HistoryHandler implements HttpHandler {
        HistoryHandler() {
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            HttpTaskServer.writeResponse(httpExchange, (new Gson()).toJson(Managers.getDefaultHistory().getHistory()), 200);
        }
    }

    static class TaskHandler implements HttpHandler {
        private final Class<? extends Task> operatingClass;
        private final Class<? extends Task> creatingClass;

        TaskHandler(Class<? extends Task> operatingClass, Class<? extends Task> creatingClass) {
            this.operatingClass = operatingClass;
            this.creatingClass = creatingClass;
        }

        private static int decodeTaskId(URI uri) {
            String[] path = uri.getPath().split("/");

            int result;
            try {
                result = Integer.parseInt(path[path.length - 1]);
            } catch (NumberFormatException var4) {
                result = 0;
            }

            return result;
        }

        private void handleDelete(HttpExchange httpExchange) throws IOException, ManagerSaveException {
            TaskManager taskManager = Managers.getDefault();
            Integer taskId2del = new Integer(decodeTaskId(httpExchange.getRequestURI()));
            Task task2del = taskManager.getTaskById(taskId2del);
            if (task2del != null && task2del.getClass() == this.operatingClass) {
                taskManager.deleteTaskById(task2del.getId());
                HttpTaskServer.writeResponse(httpExchange, "", 202);
            } else {
                HttpTaskServer.writeResponse(httpExchange, "", 400);
            }
        }

        private void handleGet(HttpExchange httpExchange) throws IOException, ManagerSaveException {
            TaskManager taskManager = Managers.getDefault();
            Integer taskId2get = new Integer(decodeTaskId(httpExchange.getRequestURI()));
            Task task2get = taskManager.getTaskById(taskId2get);
            if (task2get != null && task2get.getClass() == this.operatingClass) {
                HttpTaskServer.writeResponse(httpExchange, (new Gson()).toJson(task2get), 200);
            } else {
                HttpTaskServer.writeResponse(httpExchange, "", 400);
            }

        }

        private void handlePost(HttpExchange httpExchange) throws IOException {
            TaskManager taskManager = Managers.getDefault();
            Gson gson = new Gson();

            try {
                Task task2post = (Task) gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET), this.creatingClass);
                Task currentTask = null;
                if (task2post.getId() != 0) {
                    currentTask = taskManager.getTaskById(task2post.getId());
                }

                if (currentTask == null) {
                    currentTask = (Task) this.operatingClass.getDeclaredConstructor().newInstance();
                    currentTask.copyFrom(task2post);
                    taskManager.createTask(currentTask);
                } else {
                    taskManager.updateTask(task2post);
                }

                HttpTaskServer.writeResponse(httpExchange, gson.toJson(currentTask.getId()), 200);
            } catch (Exception var6) {
                HttpTaskServer.writeResponse(httpExchange, var6.getMessage(), 400);
            }

        }

        public void handle(HttpExchange httpExchange) throws IOException {
            String var2 = httpExchange.getRequestMethod();
            byte var3 = -1;
            switch (var2.hashCode()) {
                case 70454:
                    if (var2.equals("GET")) {
                        var3 = 1;
                    }
                    break;
                case 2461856:
                    if (var2.equals("POST")) {
                        var3 = 2;
                    }
                    break;
                case 2012838315:
                    if (var2.equals("DELETE")) {
                        var3 = 0;
                    }
            }

            switch (var3) {
                case 0:
                    try {
                        this.handleDelete(httpExchange);
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        this.handleGet(httpExchange);
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    this.handlePost(httpExchange);
                    break;
                default:
                    HttpTaskServer.writeResponse(httpExchange, "", 400);
            }

        }
    }

    static class TasksHandler implements HttpHandler {
        TasksHandler() {
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> allTasks = Managers.getDefault().getAllTasks();
            Gson gson = new Gson();
            HttpTaskServer.writeResponse(httpExchange, gson.toJson(allTasks), 200);
        }
    }

    static class RootHandler implements HttpHandler {
        RootHandler() {
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(200, 0L);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><title>");
            sb.append(this.getClass().getName());
            sb.append("</title></head>");
            sb.append(Instant.now().toString());
            sb.append("<br/>");
            sb.append("<ul>");
            Iterator var3 = HttpTaskServer.handlers.keySet().iterator();

            while (var3.hasNext()) {
                String path = (String) var3.next();
                sb.append("<li><a href=\"");
                sb.append(path);
                sb.append("\">");
                sb.append(path);
                sb.append("</a></li>");
            }
            sb.append("</ul>");
            sb.append("</html>");
            OutputStream os = httpExchange.getResponseBody();

            try {
                os.write(sb.toString().getBytes());
            } catch (Throwable var7) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }
                throw var7;
            }
            if (os != null) {
                os.close();
            }

        }
    }
}
