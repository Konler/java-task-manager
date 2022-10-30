import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasks = new ArrayList<>();
    private String status;

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    void deleteSubtaskByIdFromEpic(Integer subtaskId) {
        subTasks.remove(subtaskId);

    }
}
