package net.loganford.noideaengine.scripting.engine.javascript;

import net.loganford.noideaengine.scripting.engine.GraalTools;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class JavaTools extends GraalTools {

    public JavaTools(Context context) {
        super(context);
    }

    @Override
    protected int getFunctionParameterCount(Value value) {
        int paramCount = 0;
        if(value.canExecute() && value.toString().startsWith("function(")) {
            String source = value.toString();
            int start = source.indexOf("(");
            int end = source.indexOf(")");

            if(start != -1 && end != -1 && start < end) {
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
}
