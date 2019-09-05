'use strict';
const ScriptedEntity = Java.type('net.loganford.noideaengine.scripting.engine.javascript.JsScriptEngineTest$ScriptedEntity');

function getClass() {
    return Java.extend(ScriptedEntity, {
        onCreate: function(game, scene) {
            Java.call();
            this.setPos(500 * Math.random(), 500 * Math.random());
            this.setSprite(game.getSpriteManager().get('test_sprite'));
        }
    });
}