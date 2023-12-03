package manager;

import server.KVServer;

import java.net.URI;
import java.net.URISyntaxException;


public class Managers {
    private final static TaskManager defaultManager = new InMemoryTaskManager();
    public static URI DEFAULT_URI;

    static {
        try {
            DEFAULT_URI = new URI("http://localhost:" + KVServer.PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private static TaskManager taskManager;

    static {
        try {
            taskManager = new HttpTaskManager(DEFAULT_URI);
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    public static TaskManager getDefault() {
        return defaultManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static UserManager getUserDefault() {
        return new InMemoryUserManager();
    }
}


