package net.loganford.noideaengine.scripting;

import net.loganford.noideaengine.utils.file.DataSource;

public abstract class ScriptEngine {
    public abstract Script loadScript(DataSource dataSource);
}
