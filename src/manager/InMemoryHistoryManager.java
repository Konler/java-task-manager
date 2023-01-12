package manager;

import task.Task;

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


    List<Task> getTasks() {
        List<Task> taskHistory = new ArrayList<>();
        Node newNode = head;
        while (newNode != null) {
            taskHistory.add((Task) newNode.getTask());
            newNode = newNode.getNext();
        }
        return taskHistory;
    }

    private void removeNode(Node node) {
        if (node != null) {
            historyHash.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }

            if (prev != null) {
                prev.setNext(next);
            }

            if (next != null) {
                next.setPrev(prev);
            }
        }
    }
}