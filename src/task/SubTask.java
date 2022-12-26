package task;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Integer epicId,Status status) {
        super(name, description, status);
        this.epicId=epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
