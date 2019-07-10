package net.loganford.noideaengine.resources;

/**
 * Abstract class for resources which are cloned upon creation so modifications do not affect every other
 * resource of the same type.
 */
public abstract class PrototypeResource extends Resource implements Cloneable {

    public PrototypeResource clone() throws CloneNotSupportedException {
        return (PrototypeResource) super.clone();
    }
}
