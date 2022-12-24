package manager;

import tasks.Task;

import java.util.*;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    public static Map<Integer, Node<Task>> historyHash = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size;

    @Override
    public void add(Task task) {
        if (historyHash.containsKey(task.getId())) {
            remove(task.getId());
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> arrayList = getTasks();
        return arrayList;
    }

    @Override
    public void remove(int id) {
        if (historyHash.containsKey(id)) {
            removeNode(historyHash.get(id));
            historyHash.remove(id);
        } else {
            System.out.println("Такой задачи нет в истории просмотров");
        }
    }

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        tail = new Node<>(oldTail, task, null);
        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
            tail.prev = oldTail;
        }
        size++;
        InMemoryHistoryManager.historyHash.put(task.getId(), tail);
    }

    public int size() {
        return this.size;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        if (prevNode == null) {
            head = nextNode;

        } else {
            prevNode.next = node.next;
        }
        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = node.prev;
        }
        node.data = null;
        node.next = null;
        node.prev = null;
    }
}