package com.gameet.global.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.global.entity.AccessLog;
import com.gameet.global.exception.CustomException;
import com.gameet.global.repository.AccessLogRepository;
import com.gameet.user.entity.User;
import com.gameet.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessLogAspect {

    private final AccessLogRepository accessLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.gameet.global.annotation.AccessLoggable)")
    public Object aroundLogUserAccess(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return point.proceed();
        }

        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        String action = getActionFromAnnotation(point);
        String ipAddress = getClientIp(request);

        UserPrincipal currentUser = getCurrentUser();
        User user = null;
        if (currentUser != null) {
            user = userRepository.getReferenceById(currentUser.getUserId());
        }

        String requestMethod = getRequestMethod(request);
        String requestUrl = getRequestUrl(request);

        String requestBody = getRequestBody(point);
        int responseStatus;
        String responseBody;

        Object result = null;
        Exception caughtException = null;

        try {
            result = point.proceed();
            responseStatus = (response != null) ? response.getStatus() : 200;
            responseBody = getResponseBody(result);

        } catch (Exception e) {
            caughtException = e;
            if (e instanceof CustomException customException) {
                responseStatus = customException.getErrorCode().getStatus().value();
                responseBody = customException.getErrorCode().getMessage();
            } else if (e instanceof IllegalArgumentException) {
                responseStatus = HttpStatus.BAD_REQUEST.value();
                responseBody = e.getMessage();
            } else {
                responseStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
                responseBody = e.getMessage();
            }
        }

        AccessLog accessLog = AccessLog.builder()
                .user(user)
                .action(action)
                .ipAddress(ipAddress)
                .requestMethod(requestMethod)
                .requestUrl(requestUrl)
                .request(requestBody)
                .responseStatus(responseStatus)
                .response(responseBody)
                .build();

        accessLogRepository.save(accessLog);

        if (caughtException != null) {
            throw caughtException;
        }

        return result;
    }

    private String getActionFromAnnotation(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        AccessLoggable accessLoggable = method.getAnnotation(AccessLoggable.class);
        return accessLoggable != null ? accessLoggable.action() : "";
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.isEmpty()) {
            return ipAddress.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getRequestMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private String getRequestUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        return null;
    }

    private String getResponseBody(Object result) {
        if (result == null) {
            return null;
        }

        try {
            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                if (body == null) {
                    return null;
                }
                return objectMapper.writeValueAsString(body);
            } else {
                return objectMapper.writeValueAsString(result);
            }
        } catch (Exception e) {
            return safeToStringWithMasking(result);
        }
    }

    private String getRequestBody(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Object[] args = point.getArgs();

        List<Object> filteredArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof RequestParam || annotation instanceof RequestBody) {
                    filteredArgs.add(args[i]);
                    break;
                }
            }
        }

        try {
            return objectMapper.writeValueAsString(filteredArgs);
        } catch (Exception e) {
            return filteredArgs.stream()
                    .map(this::safeToStringWithMasking)
                    .collect(Collectors.joining(", "));
        }
    }

    private static final Set<String> SENSITIVE_KEYS = Set.of("password");

    private String safeToStringWithMasking(Object obj) {
        if (obj == null) return "null";

        String raw = obj.toString();

        for (String key : SENSITIVE_KEYS) {
            raw = raw.replaceAll("(?i)(" + key + "=)[^,&\\s]+", "$1****");
            raw = raw.replaceAll("(?i)(" + key + "\\s*:\\s*)[^,&\\s]+", "$1****");
        }

        return raw;
    }
}
