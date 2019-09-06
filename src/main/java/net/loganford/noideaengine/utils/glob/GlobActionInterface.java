package net.loganford.noideaengine.utils.glob;

import java.util.List;

public interface GlobActionInterface {
    void doAction(String resourceKey, List<String> captureGroups);
}