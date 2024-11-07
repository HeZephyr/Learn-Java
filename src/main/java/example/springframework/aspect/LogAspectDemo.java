package example.springframework.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@EnableAspectJAutoProxy
@Component
@Aspect
public class LogAspectDemo {
    @Pointcut("execution(* example.springframework.service.*(..))")
    private void pointCutMethod() {

    }

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("LogAspectDemo.doAround() before");
        Object result = joinPoint.proceed();
        System.out.println("LogAspectDemo.doAround() after");
        return result;
    }

    @Before("pointCutMethod()")
    public void doBefore() {
        System.out.println("LogAspectDemo.doBefore()");
    }

    @After("pointCutMethod()")
    public void doAfter() {
        System.out.println("LogAspectDemo.doAfter()");
    }

    @AfterReturning(pointcut = "pointCutMethod()", returning = "result")
    public void doAfterReturning(Object result) {
        System.out.println("LogAspectDemo.doAfterReturning() result: " + result);
    }

    @AfterThrowing(pointcut = "pointCutMethod()", throwing = "exception")
    public void doAfterThrowing(Exception exception) {
        System.out.println("LogAspectDemo.doAfterThrowing() exception: " + exception);
    }
}
