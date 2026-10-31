package util;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import model.annotation.Audit;

public class AuditLogger {
    public static void log(Class<?> clazz, String methodName) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    if (method.isAnnotationPresent(Audit.class)) {
                        Audit audit = method.getAnnotation(Audit.class);
                        String action = audit.action().isEmpty() ? methodName : audit.action();
                        System.out.println("[AUDIT LOG " + LocalDateTime.now() + "] Executed: " + action);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
