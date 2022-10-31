package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void deleteSubTaskByIdFromEpic(Integer subTaskId) {
        subTaskIds.remove(subTaskId);
    }
}
