package example.springframework.service;

import example.springframework.dao.UserDaoImpl;
import example.springframework.entity.User;
import lombok.Setter;

import java.util.List;

@Setter
public class UserServiceImpl {
    private UserDaoImpl userDao;

    public List<User> findUserList() {
        return userDao.findUserList();
    }
}
