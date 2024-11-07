package example.springframework;

import example.springframework.service.AopDemoServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("aspects_demo.xml");

        AopDemoServiceImpl service = context.getBean("demoService", AopDemoServiceImpl.class);

        service.doMethod1();
        service.doMethod2();
        try {
            service.doMethod3();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}
