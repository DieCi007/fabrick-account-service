package it.fabrick.account.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class LoggingAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)"
            + "|| @annotation(it.fabrick.account.annotation.LogChain)")
    public void logChain() {
    }

    @Around("logChain()")
    public Object logChain(ProceedingJoinPoint joinPoint) throws Throwable {
        var codeSignature = (CodeSignature) joinPoint.getSignature();
        var paramNames = codeSignature.getParameterNames();
        var paramValues = joinPoint.getArgs();

        var params = new ArrayList<String>();
        for (int i = 0; i < paramValues.length; i++) {
            params.add(paramNames[i] + "=" + paramValues[i]);
        }

        log.info("{}.{}: \t{}",
                codeSignature.getDeclaringType().getSimpleName(),
                codeSignature.getName(),
                params);

        return joinPoint.proceed();
    }

    @AfterReturning(value = "logChain()", returning = "response")
    public void validateApiResponse(Object response) {
        log.info("response: {}", response);
    }
}
