package net.loganford.noideaengine.utils.glob;

abstract class Token {
    private StringBuilder builder;

    public Token() {
        builder = new StringBuilder();
    }

    public void consume(char character) {
        builder.append(character);
    }

    public abstract String getTokenName();
    public abstract Token[] getNextTokens();
    public abstract String getTokenRegex();
    public abstract String getGlobRegex();

    @Override
    public String toString() {
        return builder.toString();
    }

    public boolean canConsume(char character) {
        return (toString()+character).matches(getTokenRegex());
    }
    public boolean isTerminal() {
        return true;
    }
}

