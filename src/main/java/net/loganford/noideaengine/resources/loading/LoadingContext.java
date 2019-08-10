package net.loganford.noideaengine.resources.loading;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * A LoadingContext contains a list of resource groups that need to be loaded or unloaded. This is passed into a loading
 * screen, where then the resources are taken care of.
 */
public class LoadingContext {
    @Getter @Setter private List<Integer> loadingGroups = new ArrayList<>();
    @Getter @Setter private List<Integer> unloadingGroups = new ArrayList<>();

    /**
     * Creates a loading context with loading group 0 set to loaded
     */
    public LoadingContext() {
        loadingGroups.add(0);
    }

    /**
     * Creates a loading context which will load all resource groups not currently loaded in order to handle the
     * requested GameState. Any unneeded resource groups already loaded will also be marked for unloading.
     * @param game
     * @param gameState
     */
    public LoadingContext(Game game, GameState gameState) {
        List<Integer> existing = new ArrayList<>(game.getLoadedResourceGroups());
        List<Integer> required = new ArrayList<Integer>(gameState.getRequiredResourceGroups());

        //Find existing resources that are not required
        for(int existingGroup : existing) {
            boolean found = false;
            for(int requiredGroup : required) {
                if(existingGroup == requiredGroup) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                getUnloadingGroups().add(existingGroup);
            }
        }

        //Find required resources that are not existing
        for(int requiredGroup : required) {
            boolean found = false;
            for(int existingGroup : existing) {
                if(requiredGroup == existingGroup) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                getLoadingGroups().add(requiredGroup);
            }
        }
    }

    /**
     * Checks whether loading is required. If false, then the game will proceed to the next state without a loading
     * screen.
     * @return true if loading is required.
     */
    public boolean isLoadingRequired() {
        return loadingGroups.size() > 0 || unloadingGroups.size() > 0;
    }
}
