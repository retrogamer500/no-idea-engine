'use strict'
function getClass() {
    return Java.extend(Java.type('net.loganford.noideaengine.scripting.engine.javascript.JsScriptEngineTest$ScriptedEntity'), {
        onCreate: function(game, scene) {
            Java.superCall();
            this.setPos(500 * Math.random(), 500 * Math.random());
            this.setSprite(game.getSpriteManager().get('test_sprite'));
        }
    });
}