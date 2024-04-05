package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Bitmap;
import net.loganford.noideaengine.shape.Cuboid;

public class BitmapBitmapIntersectionHandler implements IntersectionHandler<Bitmap, Bitmap> {
    private static Cuboid BITMAP_BOX1 = new Cuboid(0, 0, 0, 0, 0, 0);
    private static Cuboid BITMAP_BOX2 = new Cuboid(0, 0, 0, 0, 0, 0);

    @Override
    public boolean intersects(Bitmap bitmap, Bitmap bitmap2) {
        // Find bounds for bitmap within rectangle
        bitmap.getBoundingBox(BITMAP_BOX1);
        bitmap2.getBoundingBox(BITMAP_BOX2);

        int x1 = (int) Math.floor(Math.max(BITMAP_BOX1.getX(), BITMAP_BOX2.getX()));
        int x2 = (int) Math.ceil(Math.min(BITMAP_BOX1.getX() + BITMAP_BOX1.getWidth(), BITMAP_BOX2.getX() + BITMAP_BOX2.getWidth()));

        int y1 = (int) Math.floor(Math.max(BITMAP_BOX1.getY(), BITMAP_BOX2.getY()));
        int y2 = (int) Math.ceil(Math.min(BITMAP_BOX1.getY() + BITMAP_BOX1.getHeight(), BITMAP_BOX2.getY() + BITMAP_BOX2.getHeight()));

        // Iterate through bitmap
        for(int x = x1; x < x2; x++) {
            for(int y = y1; y < y2; y++) {
                int tx1 = (int) (x - BITMAP_BOX1.getX());
                int ty1 = (int) (y - BITMAP_BOX1.getY());

                int tx2 = (int) (x - BITMAP_BOX2.getX());
                int ty2 = (int) (y - BITMAP_BOX2.getY());

                if(bitmap.getBitmap()[tx1][ty1] && bitmap2.getBitmap()[tx2][ty2]) {
                    return true;
                }
            }
        }

        return false;
    }
}
