package dao;

import model.User;

import java.util.Map;

public class UserDAO {

    private final Map<Long, User> users;

    public UserDAO(final Map<Long, User> users) {
        this.users = users;
    }

    public void insert(User user) {
        users.put(user.getAadharNumber(), user);
    }

    public User get(Long id) {
        return users.get(id);
    }
}
