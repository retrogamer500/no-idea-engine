'use strict'
function getClass() {
    return JavaTools.extend(Java.type('net.loganford.noideaengine.state.entity.Entity'), {
        i:1,
        step: function(game, scene, delta) {
            this.i+=delta;
            if(this.i > 1000) {
                this.i-=1000;
                print(this.i);
            }
        }
    });
}