package net.loganford.noideaengine.state;

import lombok.Getter;
import net.loganford.nieEditorImporter.Tile;
import net.loganford.nieEditorImporter.json.Room;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Image;

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

        super.beginState(game);

        if(game.getEditorProject().getProperties().get("width") != null) {
            getView().setWidth(Integer.parseInt(game.getEditorProject().getProperties().get("width")));
        }
        if(game.getEditorProject().getProperties().get("height") != null) {
            getView().setHeight(Integer.parseInt(game.getEditorProject().getProperties().get("height")));
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

                //Todo: set custom properties

                add(e);
            }
            catch(Exception e) {
                //Todo: remove
                //throw new GameEngineException(e);
            }
        });
    }
}
