package net.loganford.noideaengine.scripting.engine.javascript;

import net.loganford.noideaengine.scripting.Script;
import org.graalvm.polyglot.Value;

import java.util.Map;

public class JsScript extends Script {

    private JsScriptEngine engine;
    private String code;
    private Map<String, Object> output;

    public JsScript(JsScriptEngine engine, String code) {
        this.engine = engine;
        this.code = code;
    }

    @Override
    public void execute(Map<String, Object> context, Map<String, Object> output) {
        if(context != null) {
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                engine.getContext().getBindings(JsScriptEngine.LANGUAGE_ID).putMember(entry.getKey(), entry.getValue());
            }
        }

        engine.getContext().eval(JsScriptEngine.LANGUAGE_ID, code);

        if(output != null) {
            for (String key : engine.getContext().getBindings(JsScriptEngine.LANGUAGE_ID).getMemberKeys()) {
                Value member = engine.getContext().getBindings(JsScriptEngine.LANGUAGE_ID).getMember(key);
                if(member.canExecute()) {
                    output.put(key, member);
                }
                else {
                    output.put(key, member.as(Object.class));
                }
            }
        }
    }
}
