package net.loganford.noideaengine.utils.glob;

import java.util.regex.Pattern;

class Word extends Token {
    @Override
    public String getTokenName() {
        return "string";
    }

    @Override
    public Token[] getNextTokens() {
        return new Token[]{new Wildcard(), new BeginOption()};
    }

    @Override
    public String getTokenRegex() {
        return "^[^\\*\\{\\}]+$";
    }

    @Override
    public String getGlobRegex() {
        return Pattern.quote(toString());
    }
}
