package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[0];
        }

        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Check fields including inherited superclass fields
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);

                    // Check @Positive rule
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive = field.getAnnotation(Positive.class);
                        if (value instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add(field.getName() + " " + positive.message());
                            }
                        }
                    }

                    // Check @MaxLength rule
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength maxLength = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str) {
                            if (str.length() > maxLength.value()) {
                                errors.add(field.getName() + " length exceeds maximum of " + maxLength.value());
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Could not access field: " + field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
