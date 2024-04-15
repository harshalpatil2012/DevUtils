import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JpaPerformanceLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(JpaPerformanceLoggingAspect.class);

    @Around("execution(* javax.persistence.EntityManager.*(..)) || execution(* org.springframework.data.jpa.repository.*.*(..))")
    public Object logJpaPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        logger.info("JPA Method: {} | Execution Time: {} ms", 
                    joinPoint.getSignature(), (endTime - startTime));

        return result;
    }
}
