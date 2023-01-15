package task;

import manager.TypeTasks;
import user.User;

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
    protected User user;
    private static Instant lastTaskTimesUpdate;


    public Task(String name, String description, Status status, Duration duration, Instant startTime, User user) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.user = user;
    }

    public Task(Integer id, String name, String description, Status status, Duration duration, Instant startTime, User user) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.user = user;
    }

    public Task(Task task) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.user = task.user;
    }

    public Task(String name, String description, Status status, Instant startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public Task(String name, String description, Instant startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void copyFrom(Task task2copy) {
        this.name = task2copy.name;
        this.description = task2copy.description;
        this.status = task2copy.status;
        this.setDuration(task2copy.duration);
        this.setStartTime(task2copy.startTime);
        updateLastTaskTimesUpdate();
    }

    protected static void updateLastTaskTimesUpdate() {
        lastTaskTimesUpdate = Instant.now();
    }

    public User getUser() {
        return user;
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

    public Instant getEndTime() {
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

    public void setName(String s) {
        this.name = s;
    }
}
