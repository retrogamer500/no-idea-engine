package net.loganford.noideaengine.utils.json;

import net.loganford.noideaengine.GameEngineException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Iterator;

public class JsonValidator {
    public static void validateThenThrow(Object object) {
        JsonValidationResult result = validate(object);
        if(!result.isValid()) {
            StringBuilder builder = new StringBuilder();
            builder.append("The required fields are required but missing from the JSON body:");
            for(String field : result.getMissingRequiredFields()) {
                builder.append("\n").append(field);
            }
            throw new GameEngineException(builder.toString());
        }
    }

    public static JsonValidationResult validate(Object object) {
        JsonValidationResult result = new JsonValidationResult();
        validate(object, "", result);
        return result;
    }

    private static void validate(Object object, String path, JsonValidationResult result) {
        if(!path.isEmpty()) {
            path = path + ".";
        }
        try {
            Class clazz = object.getClass();

            while(!clazz.equals(Object.class)) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        if (annotation instanceof Required) {
                            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                                result.getMissingRequiredFields().add(path + field.getName());
                            }
                        }
                    }

                    if (value instanceof Iterable) {
                        Iterator iter = ((Iterable) value).iterator();
                        int index = 0;
                        while (iter.hasNext()) {
                            Object arrayObject = iter.next();
                            if (!arrayObject.getClass().getName().startsWith("java.")) {
                                validate(arrayObject, path + field.getName() + "[" + index + "]", result);
                            }
                            index++;
                        }
                    } else if (!"this$1".equals(field.getName()) && value != null && !value.getClass().getName().startsWith("java.")) {
                        validate(value, path + field.getName(), result);
                    }
                }

                clazz = clazz.getSuperclass();
            }
        }
        catch(Exception e) {
            throw new GameEngineException("Unable to validate JSON fields", e);
        }
    }
}
