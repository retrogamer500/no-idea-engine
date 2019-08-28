package net.loganford.noideaengine;

import org.graalvm.polyglot.Context;
import org.junit.Test;

public class TestScripts {
    @Test
    public void test() {
        try (Context context = Context.create()) {
            context.eval("js", "print('Hello JavaScript!');");
        }
    }
}