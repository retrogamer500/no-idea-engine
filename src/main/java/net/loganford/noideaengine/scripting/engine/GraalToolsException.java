package net.loganford.noideaengine.scripting.engine;

public class GraalToolsException extends RuntimeException {
    public GraalToolsException() {
        super();
    }
    public GraalToolsException(String reason) {
        super(reason);
    }
    public GraalToolsException(String reason, Exception e) {
        super(reason, e);
    }
}
