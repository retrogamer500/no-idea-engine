package net.loganford.noideaengine.audio;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.lwjgl.openal.AL11;

@Log4j2
public class Audio extends Resource implements UnsafeMemory {
    @Getter private int bufferId;
    @Getter private int sampleRate;
    @Getter private int sampleLength;
    private AudioSystem audioSystem;

    public Audio(AudioSystem audioSystem, int bufferId, int sampleRate, int sampleLength) {
        this.audioSystem = audioSystem;
        this.bufferId = bufferId;
        this.sampleRate = sampleRate;
        this.sampleLength = sampleLength;
    }

    @Scriptable
    public Playback play() {
        return play(1f);
    }

    @Scriptable
    public Playback play(float gain) {
        return play(gain, false);
    }

    @Scriptable
    public Playback loop() {
        return loop(1f);
    }

    @Scriptable
    public Playback loop(float gain) {
        return play(gain, true);
    }

    public float durationSeconds() {
        return (float)sampleLength / sampleRate;
    }

    public long durationMillis() {
        return (int)(1000f * sampleLength / sampleRate);
    }

    private Playback play(float gain, boolean loop) {
        return audioSystem.playAudio(this, gain, loop);
    }

    @Override
    public void freeMemory() {
        AL11.alDeleteBuffers(bufferId);
    }
}
