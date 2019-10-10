package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Cuboid;

public class CuboidCuboidIntersectionHandler implements IntersectionHandler<Cuboid, Cuboid> {
    @Override
    public boolean intersects(Cuboid cube1, Cuboid cube2) {
        if(cube2.getX() + cube2.getWidth() < cube1.getX()) {
            return false;
        }
        if(cube2.getX() > cube1.getX() + cube1.getWidth()) {
            return false;
        }
        if(cube2.getY() + cube2.getHeight() < cube1.getY()) {
            return false;
        }
        if(cube2.getY() > cube1.getY() + cube1.getHeight()) {
            return false;
        }
        if(cube2.getZ() + cube2.getDepth() < cube1.getZ()) {
            return false;
        }
        if(cube2.getZ() > cube1.getZ() + cube1.getDepth()) {
            return false;
        }

        return true;
    }
}
