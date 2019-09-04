package net.loganford.noideaengine.audio;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.scripting.Scriptable;
import org.lwjgl.openal.AL11;

@Log4j2
public class Playback {
    @Getter private long durationMillis;
    @Getter private long startTime;
    @Getter private boolean loop;
    private boolean stopped = true;
    private int sourceId;



    public Playback(Audio audio, int sourceId, float gain, boolean loop) {
        this.sourceId = sourceId;
        this.loop = loop;
        startTime = System.currentTimeMillis();
        durationMillis = audio.durationMillis();

        AL11.alSourcei(sourceId, AL11.AL_BUFFER, audio.getBufferId());
        AL11.alSourcef(sourceId, AL11.AL_GAIN, gain);
        AL11.alSourcei(sourceId, AL11.AL_LOOPING, loop ? 1 : 0);
        AL11.alSourcePlay(sourceId);

        System.currentTimeMillis();
    }

    @Scriptable
    public boolean isPlaying() {
        return !stopped && (System.currentTimeMillis() <= startTime + durationMillis);
    }

    @Scriptable
    public void stop() {
        AL11.alSourceStop(sourceId);
        stopped = true;
    }
}
