package net.loganford.noideaengine.scripting.engine.javascript;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import net.loganford.noideaengine.scripting.Scriptable;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

public class JavaTools {
    //Maybe make this thread-local in the future to better support async code
    private static Stack<Callable> superStack = new Stack<>();
    static {
        superStack.push(null);
    }

    private static Value callMethodFunction;

    private Context context;

    public JavaTools(Context context) {
        this.context = context;

        /*We need this intermediate call between java and javascript in order to populate the 'this' keyword with the
        * target entity.*/
        String callMethodString = "(function(fun, thiz, args) {\n" +
                                      "return fun.apply(thiz, args);\n" +
                                  "})";
        callMethodFunction = context.eval(JsScriptEngine.LANGUAGE_ID, callMethodString);
    }

    @Scriptable
    public Class extend(Class<?> clazz, Value value) {

        if(!clazz.isAnnotationPresent(Scriptable.class)) {
            throw new JavaToolsException("Cannot extend class without @Scriptable annotation");
        }

        Map<Field, Object> defaultFields = new HashMap<>();
        Map<String, Object> defaultFieldStrings = new HashMap<>();

        JavaTools.ConstructorInterceptor constructorInterceptor = new JavaTools.ConstructorInterceptor(defaultFields);

        ByteBuddy buddy = new ByteBuddy();
        DynamicType.Builder builder = buddy.subclass(clazz);

        builder = builder.constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(constructorInterceptor)))
                .annotateMethod(AnnotationDescription.Builder.ofType(Scriptable.class).build());

        for(String key : value.getMemberKeys()) {
            Value member = value.getMember(key);
            if(member.canExecute()) {
                builder = handleMethod(builder, clazz, key, member, context);
            }
            else {
                builder = handleField(builder, clazz, key, member, defaultFieldStrings);
            }
        }

        Class<?> result = builder
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        for(Map.Entry<String, Object> entry : defaultFieldStrings.entrySet()) {
            String key = entry.getKey();
            try {
                Field field = result.getField(key);
                defaultFields.put(field, entry.getValue());
            } catch (NoSuchFieldException e) {
                throw new JavaToolsException("Unable to set field: " + key + ".", e);
            }

        }

        return result;
    }

    @Scriptable
    public Object call() throws Exception {
        Callable<?> callable = superStack.peek();

        if(callable == null) {
            throw new JavaToolsException("Tried to call a super method in a method which doesn't override another method!");
        }

        return callable.call();
    }

    @Scriptable
    public Class type(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    private DynamicType.Builder handleMethod(DynamicType.Builder builder, Class clazz, String key, Value member, Context context) {
        JavaTools.Interceptor interceptor = new JavaTools.Interceptor(member, context);
        int paramCount = getFunctionParameterCount(member);



        if(!classHasMethod(clazz, key, paramCount)) {
            //Declare a method
            Type[] params = new Type[paramCount];
            for(int i = 0; i < paramCount; i++) {
                params[i] = Object.class;
            }
            return builder.defineMethod(key, Object.class, Modifier.PUBLIC)
                    .withParameters(params)
                    .intercept(MethodDelegation.to(interceptor))
                    .annotateMethod(AnnotationDescription.Builder.ofType(Scriptable.class).build());
        }
        else {
            //Override a method
            return builder.method(ElementMatchers.named(key).and(ElementMatchers.isAnnotatedWith(Scriptable.class)))
                    .intercept((MethodDelegation.to(interceptor)))
                    .annotateMethod(AnnotationDescription.Builder.ofType(Scriptable.class).build());
        }
    }

    private DynamicType.Builder handleField(DynamicType.Builder builder, Class clazz, String key, Value member, Map<String, Object> defaultFieldStrings) {
        try {
            builder = builder.defineField(key, Object.class, Modifier.PUBLIC)
                    .annotateField(AnnotationDescription.Builder.ofType(Scriptable.class).build());

            String fieldKey = key;
            Object fieldValue = member.as(Object.class);
            defaultFieldStrings.put(fieldKey, fieldValue);

            return builder;
        }
        catch(Exception e) {
            throw new JavaToolsException("Unable to create field: " + key + ".", e);
        }
    }

    private boolean classHasMethod(Class clazz, String methodName, int paramCount) {
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            if(method.getName().equals(methodName) && method.getParameterCount() == paramCount) {
                return true;
            }
        }

        return false;
    }

    private int getFunctionParameterCount(Value value) {
        int paramCount = 0;
        if(value.canExecute() && value.toString().startsWith("function(")) {
            String source = value.toString();
            int start = source.indexOf("(");
            int end = source.indexOf(")");

            if(start != -1 && end != -1 && start < end) {
                if(start + 1 == end) {
                    return 0;
                }

                for(int i = start; i < end; i++) {
                    if(source.charAt(i) == ',') {
                        paramCount++;
                    }
                }
                paramCount++;
            }
        }
        return paramCount;
    }

    public static class ConstructorInterceptor {
        private Map<Field, Object> defaultFields;

        private ConstructorInterceptor(Map<Field, Object> defaultFields) {
            this.defaultFields = defaultFields;
        }

        @RuntimeType
        public void construct(@AllArguments Object[] allArguments, @This Object target) throws Exception {
            for(Map.Entry<Field, Object> entry : defaultFields.entrySet()) {
                entry.getKey().set(target, entry.getValue());
            }
        }
    }

    public static class Interceptor {
        private Value member;
        private Context context;

        public Interceptor(Value member, Context context) {
            this.member = member;
            this.context = context;
        }

        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments,  @This Object target) {
            superStack.push(null);
            Object result = callMethodFunction.execute(member, target, allArguments);
            superStack.pop();
            return result;
        }

        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments,  @This Object target, @SuperCall Callable<?> superCall) {
            superStack.push(superCall);
            Object result = callMethodFunction.execute(member, target, allArguments);
            superStack.pop();
            return result;
        }
    }
}
