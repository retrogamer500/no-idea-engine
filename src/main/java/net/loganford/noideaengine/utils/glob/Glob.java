package net.loganford.noideaengine.utils.glob;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class Glob {
    public static Pattern globToRegex(String input) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(input);
        Pattern pattern;
        try {
            pattern = Pattern.compile(tokens.stream().map(Token::getGlobRegex).collect(Collectors.joining()));
        }
        catch(PatternSyntaxException | Tokenizer.TokenError e) {
            throw new GlobException();
        }
        return pattern;
    }

    public static class GlobException extends RuntimeException {}
}
