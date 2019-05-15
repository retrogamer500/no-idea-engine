package net.loganford.noideaengine.audio;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import org.lwjgl.openal.AL11;

@Log4j2
public class Playback implements UnsafeMemory {
    @Getter private int sourceId;
    @Getter private float duration;
    @Getter private boolean loop;
    private boolean sourceExists = true;

    public Playback(int sourceId, float duration, boolean loop) {
        this.sourceId = sourceId;
        this.duration = duration;
        this.loop = loop;

        //Somewhat inefficient... Maybe replace with a circular buffer and manage that manually if this is too slow.
        if (!loop) {
            new Thread(() -> {
                try {
                    Thread.sleep((int) ((duration + 1) * 1000));
                } catch (InterruptedException e) {
                    log.error("Playback thread interrupted.", e);
                } finally {
                    freeMemory();
                }
            }).start();
        }
    }

    public void stop() {
        AL11.alSourceStop(sourceId);
        freeMemory();
    }

    @Override
    public void freeMemory() {
        if(sourceExists) {
            AL11.alDeleteSources(sourceId);
            sourceExists = false;
        }
    }
}
