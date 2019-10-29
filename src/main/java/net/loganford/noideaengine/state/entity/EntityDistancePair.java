package net.loganford.noideaengine.state.entity;

import lombok.Getter;

public class EntityDistancePair<C> {

    @Getter private C entity;
    @Getter private float distanceSqr;

    public EntityDistancePair(C entity, float distanceSqr) {
        this.entity = entity;
        this.distanceSqr = distanceSqr;
    }

    public float getDistance() {
        return (float)Math.sqrt(distanceSqr);
    }
}
