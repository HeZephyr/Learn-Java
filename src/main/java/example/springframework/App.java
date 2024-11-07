package example.springframework;

import example.springframework.entity.User;
import example.springframework.service.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

/**
 * Main application class that demonstrates basic Spring Framework dependency injection
 * and retrieves a list of users from a UserService implementation.
 */
public class App {
    public static void main(String[] args) {
        // Initialize the Spring application context with XML configuration files
        ApplicationContext context =
                new AnnotationConfigApplicationContext("example.springframework");

        // Retrieve the UserService bean instance by its ID from the application context
        UserServiceImpl service = context.getBean(UserServiceImpl.class);

        // Call a method on the retrieved service instance to get a list of users
        List<User> userList = service.findUserList();

        // Print each user's name and age from the user list
        userList.forEach(user -> System.out.println(user.getName() + " " + user.getAge()));
    }
}