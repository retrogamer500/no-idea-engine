package net.loganford.noideaengine.config.json;

import lombok.Data;
import net.loganford.noideaengine.config.Required;

@Data
public class GameConfig {
    @Required private Resources resources;
}
