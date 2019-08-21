package net.loganford.noideaengine.utils.glob;

class Wildcard extends Token {
    @Override
    public String getTokenName() {
        return "*";
    }

    @Override
    public Token[] getNextTokens() {
        return new Token[]{new Word()};
    }

    @Override
    public String getTokenRegex() {
        return "^\\*\\*?$";
    }

    @Override
    public String getGlobRegex() {
        if(toString().length() == 1) {
            return "([^\\/]*)";
        }
        else {
            return "(.*)";
        }
    }
}
