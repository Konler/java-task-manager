package tasks;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
