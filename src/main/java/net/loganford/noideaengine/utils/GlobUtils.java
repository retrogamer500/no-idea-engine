package net.loganford.noideaengine.utils;

public class GlobUtils {
    public static String regexFromGlob(String glob) {
        StringBuilder builder = new StringBuilder();
        builder.append("^");
        for(int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            char nc = i < glob.length() - 1 ? glob.charAt(i+1) : '\u0000';

            if(c == '*') {
                if(nc == '*') {
                    builder.append("(.*)");
                    i++;
                }
                else {
                    builder.append("([^/\\]*)");
                }
            }
            else if(c == '\\') {
                builder.append("\\\\");
            }
            else if(c == '.') {
                builder.append("\\.");
            }
            else if(c == '(') {
                builder.append("\\(");
            }
            else if(c == ')') {
                builder.append("\\)");
            }
            else {
                builder.append(c);
            }
        }

        builder.append("$");
        return builder.toString();
    }
}
