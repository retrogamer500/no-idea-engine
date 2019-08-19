package net.loganford.noideaengine.utils.file;

import lombok.extern.log4j.Log4j2;

/**
 * A resource mapper allows the conversion between a string and a resource. This abstracts away the method of how game
 * resources are stored and allows us to store these resources in files, zip files, within the jar, or potentially
 * anywhere else.
 */
@Log4j2
public abstract class AbstractResourceMapper {
    /**
     * Converts a string into a resource.
     * @param resourceKey a string representing the resource
     * @return the resource, all ready for reading data
     */
    public abstract AbstractResource get(String resourceKey);

    /**
     * Expands a glob.
     * @param resourceKey A resource key. If the resource key does not contain a glob, nothing will happen. The glob MUST
     *                    be a single asterisk at the end of the resourceKey. Globs elsewhere are not supported.
     * @param globAction an action to perform with the glob
     */
    public void expandGlob(String resourceKey, globActionInterface globAction) {}

    public interface globActionInterface {
        void doAction(String fullResourceKey, String glob);
    }
}
