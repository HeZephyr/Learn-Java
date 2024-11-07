package example.springframework.service;

import example.springframework.dao.UserDaoImpl;
import example.springframework.entity.User;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service // Marks this class as a Service, indicating that it contains business logic and serves as a service layer in the application.
@AllArgsConstructor
public class UserServiceImpl {
    private final UserDaoImpl userDao;

    public List<User> findUserList() {
        return userDao.findUserList();
    }
}
