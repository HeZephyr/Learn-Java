package example.springframework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class LogAspect {

    /*
     * Defines an aspect that applies to all methods in the `service` package.
     * This advice is triggered around each method invocation, providing
     * a way to add behavior before and after method execution.
     */
    @Around("execution(* example.springframework.service.*.*(..))")
    public Object businessService(ProceedingJoinPoint pjp) throws Throwable {
        // Retrieve the method signature from the join point to access method details
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        // Log the name of the method being executed
        System.out.println("Method name: " + method.getName());

        // Proceed with the original method execution and return its result
        return pjp.proceed();
    }
}