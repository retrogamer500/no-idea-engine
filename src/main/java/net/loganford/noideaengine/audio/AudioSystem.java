package net.loganford.noideaengine.audio;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.lwjgl.openal.*;

import java.util.stream.IntStream;

import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;

@Log4j2
public class AudioSystem implements UnsafeMemory {
    private static final int MIN_SOURCE_SIZE = 32;
    private static final int MAX_SOURCE_SIZE = 256;

    private long device;
    private long context;

    private Playback[] sources;
    private int sourcePointer = -1;

    public AudioSystem() {
        log.info("Initializing OpenAL");
        String defaultDeviceName = ALC11.alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        log.info("Device name: " + defaultDeviceName);

        device = ALC11.alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        context = ALC11.alcCreateContext(device, attributes);
        ALC11.alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities  = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL11) {
            throw new GameEngineException("OpenAL 1.1 not supported");
        }

        int audioError = AL11.alGetError();
        if(audioError != AL11.AL_NO_ERROR) {
            throw new GameEngineException("OpenAL error: " + audioError);
        }


        int sourceNum = Math.min(Math.max(ALC11.alcGetInteger(device, ALC11.ALC_MONO_SOURCES), MIN_SOURCE_SIZE), MAX_SOURCE_SIZE);
        log.info("Max available sound channels: " + sourceNum);
        sources = new Playback[sourceNum];
        AL11.alGenSources(IntStream.range(1, sourceNum + 1).toArray());
    }

    protected Playback playAudio(Audio audio, float gain, boolean loop ) {
        int newSourceId = -1;
        int leastImportantSoundId = 0;
        long leastImportantSoundEndTime = Long.MAX_VALUE;
        for(int i = sourcePointer + 1; i < sourcePointer + 1 + sources.length; i++) {
            int j = i % sources.length;

            if(sources[j] == null) {
                sourcePointer = j;
                newSourceId = sourcePointer;
                log.info("Playing sound from new source. sourcePointer: " + sourcePointer);
                break;
            }
            else {
                Playback oldPlayback = sources[j];
                if(!oldPlayback.isPlaying()) {
                    sourcePointer = j;
                    newSourceId = sourcePointer;
                    log.info("Playing sound from existing source. sourcePointer: " + sourcePointer);
                    break;
                }
                else {
                    if(oldPlayback.getDurationMillis() + oldPlayback.getStartTime() < leastImportantSoundEndTime) {
                        leastImportantSoundEndTime = oldPlayback.getDurationMillis() + oldPlayback.getStartTime();
                        leastImportantSoundId = j;
                    }
                }
            }
        }

        if(newSourceId == -1) {
            log.warn("No more available sound channels detected. Killing sound at ID: " + leastImportantSoundId);
            newSourceId = leastImportantSoundId;
            sources[leastImportantSoundId].stop();
        }


        Playback playback = new Playback(audio, newSourceId + 1, gain, loop);
        sources[newSourceId] = playback;
        return playback;
    }

    public void stopAll() {
        log.info("Stopping sounds");
        for(int i = 0; i < sources.length; i++) {
            AL11.alSourceStop(i + 1);
        }
    }

    public void stopNonlooping() {
        log.info("Stopping non-looping sounds");
        for(int i = 0; i < sources.length; i++) {
            if(sources[i] != null && !sources[i].isLoop()) {
                AL11.alSourceStop(i + 1);
            }
        }
    }

    @Override
    public void freeMemory() {
        AL11.alDeleteSources(IntStream.range(1, sources.length + 1).toArray());
        ALC11.alcDestroyContext(context);
        ALC11.alcCloseDevice(device);
    }
}
