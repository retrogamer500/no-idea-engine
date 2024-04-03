package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditorImporter.Tile;
import net.loganford.nieEditorImporter.json.Room;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Image;

import java.lang.reflect.Field;
import java.util.Map;

@Log4j2
public class RoomEditorScene extends Scene {

    @Getter private String levelName;

    public RoomEditorScene(String levelName) {
        this.levelName = levelName;
    }

    @Override
    public void beginState(Game game) {
        if("true".equals(game.getEditorProject().getProperties().get("stretch"))) {
            setStretch(true);
        }
        else {
            if(game.getEditorProject().getProperties().get("scale") != null) {
                setScale(Float.parseFloat(game.getEditorProject().getProperties().get("scale")));
            }
        }

        super.beginState(game);

        if(isStretch()) {
            if (game.getEditorProject().getProperties().get("width") != null) {
                getView().setWidth(Integer.parseInt(game.getEditorProject().getProperties().get("width")));
            }
            if (game.getEditorProject().getProperties().get("height") != null) {
                getView().setHeight(Integer.parseInt(game.getEditorProject().getProperties().get("height")));
            }
        }


        Room room = game.getEditorProject().getRoom(levelName);

        setWidth(room.getWidth());
        setHeight(room.getHeight());
        setBackgroundColor(room.getBgColorR() / 255f, room.getBgColorG()  / 255f, room.getBgColorB() / 255f, 1f);

        room.getTileLayers().forEach(tl -> {
            Image tileImage = game.getImageManager().get(tl.getTileset().getEngineResourceKey());
            TileLayer engineLayer = addTileLayer(tileImage, tl.getTileMapWidth(), tl.getTileMapHeight(), tl.getTileset().getTileWidth(), tl.getTileset().getTileHeight(), tl.getDepth());

            for(Tile tile : tl.getTiles()) {
                engineLayer.setTile(tile.getRoomX(), tile.getRoomY(), tile.getTileX(), tile.getTileY());
            }
        });

        room.getInstances().forEach(i -> {
            try {
                Entity e = (Entity) Class.forName(i.getClassPath()).getDeclaredConstructor().newInstance();
                e.setX(i.getX());
                e.setY(i.getY());
                e.setDepth(i.getDepth());

                if(i.getCustomProperties() != null) {
                    for (Map.Entry<String, String> entry : i.getCustomProperties().entrySet()) {
                        setProperty(e, entry.getKey(), entry.getValue());
                    }
                }

                add(e);
            }
            catch(Exception e) {
                log.warn("Cannot instantiate object: " + i.getClassPath(), e);
            }
        });
    }

    private static void setProperty(Entity entity, String key, String value) {
        Class<?> clazz = entity.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                field.set(entity, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        log.warn("Cannot set entity property with key: " + key);
    }
}
