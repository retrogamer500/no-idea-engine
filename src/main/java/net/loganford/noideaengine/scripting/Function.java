package net.loganford.noideaengine.scripting;

public abstract class Function {
    public abstract void eval(Object...args);
    public abstract float evalFloat(Object...args);
    public abstract int evalInt(Object...args);
    public abstract String evalString(Object...args);
    public abstract <C> C evalObject(Class<C> clazz, Object...args);
    public abstract Function evalFunction(Object...args);
}
