package example.springframework.aspect;


import org.aspectj.lang.ProceedingJoinPoint;

public class LogAspectDemo {
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("LogAspectDemo.doAround() before");
        Object result = joinPoint.proceed();
        System.out.println("LogAspectDemo.doAround() after");
        return result;
    }

    public void doBefore() {
        System.out.println("LogAspectDemo.doBefore()");
    }

    public void doAfter() {
        System.out.println("LogAspectDemo.doAfter()");
    }

    public void doAfterReturning(Object result) {
        System.out.println("LogAspectDemo.doAfterReturning() result: " + result);
    }

    public void doAfterThrowing(Exception exception) {
        System.out.println("LogAspectDemo.doAfterThrowing() exception: " + exception);
    }
}
