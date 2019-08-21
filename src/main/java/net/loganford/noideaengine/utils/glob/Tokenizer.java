package net.loganford.noideaengine.utils.glob;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Tokenizer {

    public List<Token> tokenize(String input) {
        List<Token> tokenList = new ArrayList<>();
        Token[] nextTokens = new Token[]{new Word(), new Wildcard()};
        ParseString parseString = new ParseString(input);

        while(parseString.hasNext()) {
            char currentCharacter = parseString.get();
            Token currentToken = null;
            for(Token potentialToken : nextTokens) {
                if(potentialToken.canConsume(currentCharacter)) {
                    currentToken = potentialToken;
                }
            }

            if(currentToken == null) {
                StringBuilder error = new StringBuilder();
                error.append("Invalid character! Got: ").append(currentCharacter).append(" Expected: ");
                for(int i = 0; i < nextTokens.length; i++) {
                    if(i != 0) {
                        error.append(" OR ");
                    }
                    error.append(nextTokens[i].getTokenName());
                }
                error.append("!");
                throw new RuntimeException(error.toString());
            }

            tokenList.add(currentToken);

            while(parseString.hasNext() && currentToken.canConsume(currentCharacter)) {
                currentToken.consume(currentCharacter);
                parseString.next();
                if(parseString.hasNext()) {
                    currentCharacter = parseString.get();
                }
            }

            nextTokens = currentToken.getNextTokens();
        }

        return tokenList;
    }

    private void error(ParseString parseString, Token[] nextTokens) {
        int index = parseString.getIndex();
        char character = parseString.get();

        log.error("Unexpected character in parseString at position " + index + ": " + character + ".");
        log.error(parseString.getString());
        log.error(StringUtils.leftPad("^", 1+index));
        log.error("Expected: " + Stream.of(nextTokens).map(Token::getTokenName).collect(Collectors.joining(" OR ")) + ".");
        throw new TokenError();
    }

    public class TokenError extends RuntimeException {}

}
