'use strict'
function getClass() {
    print('Generating class!');
    var parentClass = Java.type('net.loganford.noideaengine.state.entity.Entity');
    var newClass = JavaTools.extend(parentClass, {
        i:1,
        step: function(game, scene, delta) {
            entity.i++;
            if(entity.i % 100 == 0) {
                print("Hello world!");
            }
        }
    });
    return newClass;
}
