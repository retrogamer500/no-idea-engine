package net.loganford.noideaengine.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class JsonValidationResult {
    @Getter private List<String> missingRequiredFields;

    protected JsonValidationResult() {
        missingRequiredFields = new ArrayList<>();
    }

    public boolean isValid() {
        return missingRequiredFields.size() == 0;
    }
}
