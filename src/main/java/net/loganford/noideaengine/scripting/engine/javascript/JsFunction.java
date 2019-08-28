package net.loganford.noideaengine.scripting.engine.javascript;

import net.loganford.noideaengine.scripting.Function;
import org.graalvm.polyglot.Value;

public class JsFunction extends Function {
    Value function;

    public JsFunction(Value function) {
        this.function = function;
    }

    @Override
    public void eval(Object... args) {
        function.execute(args);
    }

    @Override
    public float evalFloat(Object... args) {
        return function.execute(args).asFloat();
    }

    @Override
    public int evalInt(Object... args) {
        return function.execute(args).asInt();
    }

    @Override
    public String evalString(Object... args) {
        return function.execute(args).asString();
    }

    @Override
    public <C> C evalObject(Class<C> clazz, Object... args) {
        return function.execute(args).as(clazz);
    }

    @Override
    public Function evalFunction(Object... args) {
        Value value = function.execute(args);
        if(value.canExecute()) {
            return new JsFunction(value);
        }
        return null;
    }
}
