package net.loganford.noideaengine.scripting.engine.javascript;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.scripting.ScriptEngine;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.utils.file.DataSource;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

@Log4j2
public class JsScriptEngine extends ScriptEngine {
    public static final String LANGUAGE_ID = "js";

    private Context getNewContext() {
        HostAccess hostAccess = HostAccess.newBuilder()
                .allowAccessAnnotatedBy(Scriptable.class)
                .allowImplementationsAnnotatedBy(Scriptable.class)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .build();


        Context context = Context.newBuilder()
                .allowHostAccess(hostAccess)
                .allowHostClassLookup((s)-> {
                    try {
                        return Class.forName(s).isAnnotationPresent(Scriptable.class);
                    } catch (ClassNotFoundException e) {
                        log.warn("Tried to look up non-existent class: " + s + ".");
                        return false;
                    }
                })
                .build();

        JavaTools javaTools = new JavaTools(context);
        context.getBindings("js").putMember("JavaTools", javaTools);
        context.initialize(LANGUAGE_ID);

        return context;
    }

    @Override
    public Script loadScript(DataSource dataSource) {
        return new JsScript(getNewContext(), dataSource.load());
    }
}
