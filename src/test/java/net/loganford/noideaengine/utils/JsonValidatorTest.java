package net.loganford.noideaengine.utils;

import lombok.Data;
import net.loganford.noideaengine.utils.json.JsonValidationResult;
import net.loganford.noideaengine.utils.json.JsonValidator;
import net.loganford.noideaengine.utils.json.Required;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JsonValidatorTest {
    @Test
    public void testValidator() {
        @Data
        class TestClass {

            @Data
            class TestClass2 {
                @Required
                private String f1 = "";
            }

            @Required private String f1 = "test";
            @Required private String f2 = "";
            private List<TestClass2> list;

            public TestClass() {
                list = new ArrayList<>();
                list.add(new TestClass2());
            }
        }



        JsonValidationResult result = JsonValidator.validate(new TestClass());
        System.out.println("Valid: " + result.isValid());
    }

}