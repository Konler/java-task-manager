package task;

import java.time.Duration;
import java.time.Instant;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Status status, Instant startTime, Duration duration, Integer epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

}
