package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT_HISTORY = 10;
    private ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() >= LIMIT_HISTORY) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}

