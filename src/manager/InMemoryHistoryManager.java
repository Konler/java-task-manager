package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT_HISTORY = 10;
    private LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() >= LIMIT_HISTORY) {
            historyList.pollFirst();
        }
        historyList.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}

