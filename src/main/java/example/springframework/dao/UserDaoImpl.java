package example.springframework.dao;

import example.springframework.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository // Marks this class as a Repository, indicating that it is a Data Access Object (DAO) component responsible for data persistence.
public class UserDaoImpl {
    public List<User> findUserList() {
        return Collections.singletonList(new User("John", 30));
    }
}
