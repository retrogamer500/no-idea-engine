package net.loganford.noideaengine.utils.json;

import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

public class JsonValidatorTest {
    @Test
    public void testJsonValidator() {
        Child child = new Child();
        child.setChildString("test");

        JsonValidationResult result = JsonValidator.validate(child);

        Assert.assertEquals(1, result.getMissingRequiredFields().size());
    }

    public class Parent {
        @Required
        @Getter @Setter private String parentString;
    }

    public class Child extends Parent {
        @Required
        @Getter @Setter private String childString;
    }
}