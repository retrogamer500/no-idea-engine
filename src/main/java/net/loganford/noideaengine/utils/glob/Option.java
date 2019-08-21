package net.loganford.noideaengine.utils.glob;

class Option extends Word {
    @Override
    public Token[] getNextTokens() {
        return new Token[]{new Delimiter(), new EndOption()};
    }

    @Override
    public String getTokenRegex() {
        return "^[^\\*\\{\\},]+$";
    }
}
