package task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, Status status, Instant startTime,Duration duration) {
        super(name, description, status);

    }
    public Epic(String name, String description, Instant startTime,Duration duration) {
        super(name, description);

    }

    public List<Integer> getSubTaskIds() {

        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void deleteSubTaskByIdFromEpic(Integer subTaskId) {
        subTaskIds.remove(subTaskId);
    }

}
