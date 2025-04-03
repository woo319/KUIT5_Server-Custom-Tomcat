package db;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository {
    private static final db.MemoryUserRepository instance = new db.MemoryUserRepository();
    private final Map<String, model.User> userMap = new HashMap<>();

    private MemoryUserRepository() {}

    public static db.MemoryUserRepository getInstance() {
        return instance;
    }

    public void save(model.User user) {
        userMap.put(user.getUserId(), user);
    }

    public model.User findByUserId(String userId) {
        return userMap.get(userId);
    }

    public Map<String, User> findAll() {
        return userMap;
    }
}
