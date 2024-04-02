'use strict';
const ScriptedEntity = Java.type('net.loganford.noideaengine.misc.JsScriptEngineTest$ScriptedEntity');

function getClass() {
    return Java.extend(ScriptedEntity, {
        onCreate: function(game, scene) {
            Java.call();
            this.setPos(500 * Math.random(), 500 * Math.random());
            this.setSprite(game.getSpriteManager().get('test_sprite'));
        },
        step: function(game, scene, delta) {
            Java.call();
            this.setPos(500 * Math.random(), 500 * Math.random());
        }
    });
}