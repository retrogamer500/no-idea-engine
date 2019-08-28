package net.loganford.noideaengine.scripting.engine.javascript;

public class JavaToolsException extends RuntimeException {
    public JavaToolsException() {
        super();
    }
    public JavaToolsException(String reason) {
        super(reason);
    }
    public JavaToolsException(String reason, Exception e) {
        super(reason, e);
    }
}
