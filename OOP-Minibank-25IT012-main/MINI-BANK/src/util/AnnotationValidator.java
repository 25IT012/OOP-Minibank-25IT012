package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import model.annotation.MaxLength;
import model.annotation.Positive;

public class AnnotationValidator {
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[0];
        }
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object val = field.get(obj);
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive pos = field.getAnnotation(Positive.class);
                        if (val instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add(field.getName() + " " + pos.message());
                            }
                        }
                    }
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength max = field.getAnnotation(MaxLength.class);
                        if (val instanceof String s && s.length() > max.value()) {
                            errors.add(field.getName() + " length exceeds maximum of " + max.value());
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Unable to access field " + field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return errors.toArray(new String[0]);
    }
}
