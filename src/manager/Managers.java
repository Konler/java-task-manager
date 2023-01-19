package manager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Managers {
    private final static TaskManager defaultManager=new InMemoryTaskManager();

            public static URI DEFAULT_URI;

    static {
        try {
            DEFAULT_URI = new URI("http://localhost:"+ KVServer.PORT);
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

    public static UserManager getUserDefault(){
        return new InMemoryUserManager();
    }

}


