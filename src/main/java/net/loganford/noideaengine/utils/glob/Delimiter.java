package net.loganford.noideaengine.utils.glob;

class Delimiter extends Token {
    @Override
    public String getTokenName() {
        return ",";
    }

    @Override
    public Token[] getNextTokens() {
        return new Token[]{new Option()};
    }

    @Override
    public String getTokenRegex() {
        return "^,$";
    }

    @Override
    public String getGlobRegex() {
        return "|";
    }
}
