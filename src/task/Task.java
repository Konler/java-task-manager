package task;

import manager.TypeTasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    private Duration duration;
    private Instant startTime;
    
    public Task(String name, String description, Status status, Instant startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime=startTime;
        this.duration=duration;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime(){
        return startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }
    

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, name, description, startTime, duration);
    }

    @Override
    public String toString() {
        return id + "," +
                TypeTasks.TASK + "," +
                name + "," +
                status + "," +
                description + "," +
                startTime.toEpochMilli() + "," +
                duration;
    }
}
