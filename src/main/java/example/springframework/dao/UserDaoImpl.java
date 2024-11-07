package example.springframework.dao;

import example.springframework.entity.User;

import java.util.Collections;
import java.util.List;

public class UserDaoImpl {
    public List<User> findUserList() {
        return Collections.singletonList(new User("John", 30));
    }
}
