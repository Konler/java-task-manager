package manager;

import task.Task;
import user.User;

import java.util.List;

public interface UserManager {
    int add(User user);

    void update(User user);

    User getById(int id);

    List<User> getAll();

    List<Task> getUserTask(int id);

    void delete(int id);

    TaskManager getTaskManager();
}
