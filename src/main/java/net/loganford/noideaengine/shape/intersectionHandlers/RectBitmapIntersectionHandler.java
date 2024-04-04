package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Bitmap;
import net.loganford.noideaengine.shape.Cuboid;
import net.loganford.noideaengine.shape.Rect;

public class RectBitmapIntersectionHandler implements IntersectionHandler<Rect, Bitmap> {
    private static Cuboid BITMAP_BOX = new Cuboid(0, 0, 0, 0, 0, 0);

    @Override
    public boolean intersects(Rect rect, Bitmap bitmap) {
        // Find bounds for bitmap within rectangle
        bitmap.getBoundingBox(BITMAP_BOX);
        int x1 = (int) Math.floor(Math.max(rect.getX(), BITMAP_BOX.getX()));
        int x2 = (int) Math.ceil(Math.min(rect.getX() + rect.getWidth(), BITMAP_BOX.getX() + BITMAP_BOX.getWidth()));

        int y1 = (int) Math.floor(Math.max(rect.getY(), BITMAP_BOX.getY()));
        int y2 = (int) Math.ceil(Math.min(rect.getY() + rect.getHeight(), BITMAP_BOX.getY() + BITMAP_BOX.getHeight()));

        // Iterate through bitmap
        for(int x = x1; x < x2; x++) {
            for(int y = y1; y < y2; y++) {
                int tx = (int) (x - BITMAP_BOX.getX());
                int ty = (int) (y - BITMAP_BOX.getY());

                if(bitmap.getBitmap()[tx][ty]) {
                    return true;
                }
            }
        }

        return false;
    }
}
