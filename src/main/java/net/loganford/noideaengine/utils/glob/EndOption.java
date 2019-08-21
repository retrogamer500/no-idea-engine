package net.loganford.noideaengine.utils.glob;

class EndOption extends Token {
    @Override
    public String getTokenName() {
        return "}";
    }

    @Override
    public Token[] getNextTokens() {
        return new Token[]{new Wildcard(), new BeginOption(), new Word()};
    }

    @Override
    public String getTokenRegex() {
        return "^\\}$";
    }

    @Override
    public String getGlobRegex() {
        return ")";
    }
}
