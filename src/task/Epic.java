package task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final ArrayList<Integer> subTaskIds = new ArrayList<>();
    private Instant endTime;


    public Epic(String name, String description, Status status, Instant startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public List<Integer> getSubTaskIds() {

        return subTaskIds;
    }



    public void deleteSubTaskByIdFromEpic(Integer subTaskId) {
        subTaskIds.remove(subTaskId);
    }

}
