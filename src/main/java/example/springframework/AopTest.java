package example.springframework;

import example.springframework.service.CglibProxyServiceImpl;
import example.springframework.service.JdkProxyServiceImpl;
import example.springframework.service.ProxyService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopTest {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext("example.springframework");

        ProxyService service = context.getBean(JdkProxyServiceImpl.class);

        service.doMethod1();
        service.doMethod2();
        try {
            service.doMethod3();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        CglibProxyServiceImpl service1 = context.getBean(CglibProxyServiceImpl.class);
        service1.doMethod1();
        service1.doMethod2();
        try {
            service1.doMethod3();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}
