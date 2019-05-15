package net.loganford.noideaengine.audio;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import org.lwjgl.openal.AL11;

@Log4j2
public class Audio implements UnsafeMemory {
    @Getter private int bufferId;
    @Getter private int sampleRate;
    @Getter private int sampleLength;

    public Audio(int bufferId, int sampleRate, int sampleLength) {
        this.bufferId = bufferId;
        this.sampleRate = sampleRate;
        this.sampleLength = sampleLength;
    }

    public Playback play() {
        return play(1f);
    }

    public Playback play(float gain) {
        return play(false, gain);
    }

    public Playback loop() {
        return loop(1f);
    }

    public Playback loop(float gain) {
        return play(true, gain);
    }

    private Playback play(boolean loop, float gain) {
        float duration = (float)sampleLength / sampleRate;
        int sourceId = AL11.alGenSources();
        AL11.alSourcei(sourceId, AL11.AL_BUFFER, bufferId);
        AL11.alSourcef(sourceId, AL11.AL_GAIN, gain);
        AL11.alSourcei(sourceId, AL11.AL_LOOPING, loop ? 1 : 0);
        AL11.alSourcePlay(sourceId);
        Playback playback = new Playback(sourceId, duration, loop);
        log.info("Sound playing. Source: " + sourceId + ". Buffer: " + bufferId + ". Duration: " + duration + ".");
        return playback;
    }

    @Override
    public void freeMemory() {
        AL11.alDeleteBuffers(bufferId);
    }
}
