package net.loganford.noideaengine.utils.glob;

import org.junit.Test;

import java.util.regex.Pattern;

public class GlobTest {
    @Test
    public void testGlob() {
        Pattern pattern = Glob.globToRegex("data/images/**.{png,jpg}");
        System.out.println(pattern.pattern());
    }

}