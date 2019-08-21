package net.loganford.noideaengine.utils.glob;

import lombok.Getter;

class ParseString {
    @Getter private String string;
    @Getter private int index;

    public ParseString(String string) {
        this.string = string;
    }

    public boolean hasNext() {
        return index < string.length();
    }

    public char get() {
        return string.charAt(index);
    }

    public void next() {
        index++;
    }
}
