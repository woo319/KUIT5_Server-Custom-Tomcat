package webserver;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository {
    private static final MemoryUserRepository instance = new MemoryUserRepository();
    private final Map<String, User> userMap = new HashMap<>();

    private MemoryUserRepository() {}

    public static MemoryUserRepository getInstance() {
        return instance;
    }

    public void save(User user) {
        userMap.put(user.getUserId(), user);
    }

    public User findByUserId(String userId) {
        return userMap.get(userId);
    }

    public Map<String, User> findAll() {
        return userMap;
    }
}
