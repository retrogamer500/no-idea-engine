package net.loganford.noideaengine;

public class GameEngineException extends RuntimeException {
    public GameEngineException(String message) {
        super(message);
    }

    public GameEngineException(Exception e) {
        super(e);
    }

    public GameEngineException(String message, Exception e) {
        super(e);
    }
}
