package net.loganford.noideaengine.utils.file;

import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * A resource mapper allows the conversion between a string and a resource. This abstracts away the method of how game
 * resources are stored and allows us to store these resources in files, zip files, within the jar, or potentially
 * anywhere else.
 */
@Log4j2
public abstract class ResourceMapper {
    /**
     * Converts a string into a resource.
     * @param resourceKey a string representing the resource
     * @return the resource, all ready for reading data
     */
    public abstract DataSource get(String resourceKey);

    /**
     * Expands a glob.
     * @param glob A resource key. If the resource key does not contain a glob, nothing will happen. The glob MUST
     *                    be a single asterisk at the end of the resourceKey. Globs elsewhere are not supported.
     * @param globAction an action to perform with the glob
     */
    public void expandGlob(String glob, GlobActionInterface globAction) {}

    public interface GlobActionInterface {
        void doAction(String resourceKey, List<String> captureGroups);
    }
}
