package net.loganford.noideaengine.scripting.engine.javascript;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.scripting.Function;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

@Log4j2
public class JsScript extends Script implements UnsafeMemory {

    private Context context;
    private Source source;

    public JsScript(Context context, String source) {
        this.context = context;
        this.source = Source.create(JsScriptEngine.LANGUAGE_ID, source);
    }

    @Override
    public void execute() {
        context.eval(source);
        executed = true;
    }

    @Override
    public float getFloat(String key) {
        if(!executed) {
            execute();
        }

        Value value = context.getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
        if(value != null && value.isNumber() && value.fitsInInt()) {
            return value.asFloat();
        }
        return 0;
    }

    @Override
    public float getInt(String key) {
        if(!executed) {
            execute();
        }

        Value value = context.getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
        if(value != null && value.isNumber() && value.fitsInInt()) {
            return value.asInt();
        }
        return 0;
    }

    @Override
    public String getString(String key) {
        if(!executed) {
            execute();
        }

        Value value = context.getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
        if(value != null && value.isString()) {
            return value.asString();
        }
        return "";
    }

    @Override
    public <C> C getObject(String key, Class<C> clazz) {
        if(!executed) {
            execute();
        }

        Value value = context.getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
        try {
            return value.as(clazz);
        }
        catch(ClassCastException | PolyglotException e) {
            log.info("Cannot cast key " + key + " as " + clazz.toString() + ".");
        }

        return null;
    }

    @Override
    public Function getFunction(String key) {
        if(!executed) {
            execute();
        }

        Value value = context.getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
        if(value.canExecute()) {
            return new JsFunction(value);
        }
        return null;
    }

    @Override
    public void freeMemory() {
        context.close(true);
    }
}
