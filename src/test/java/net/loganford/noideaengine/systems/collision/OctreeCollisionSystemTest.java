package net.loganford.noideaengine.systems.collision;

import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.shape.Cuboid;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.junit.Test;

public class OctreeCollisionSystemTest {
    @Test
    public void testOctreeCollisionSystem() {
        OctreeCollisionSystem system = new OctreeCollisionSystem(null, null, new Argument[]{});
        system.setMaxDepth(2);

        /*for(int i = 0; i < 16; i++) {
            Cuboid cuboid = new Cuboid(5, 5, 5, 1, 1, 1);
            Entity entity = new Entity();
            entity.setShape(cuboid);
            system.addEntity(entity);
        }*/

        Cuboid cuboid = new Cuboid(0, 0, 0, 1, 1, 1);
        Entity entity = new Entity();
        entity.setShape(cuboid);
        entity.setPos(3000, 3000, 3000);
        system.addEntity(entity);

        boolean result = system.collidesWith(new Cuboid(3000, 3000, 3000, 1, 1, 1), Entity.class);
        System.out.println("test");
    }

}