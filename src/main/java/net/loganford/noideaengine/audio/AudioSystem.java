package net.loganford.noideaengine.audio;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import org.lwjgl.openal.*;

import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;

@Log4j2
public class AudioSystem implements UnsafeMemory {
    private long device;
    private long context;

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
    }

    @Override
    public void freeMemory() {
        ALC11.alcDestroyContext(context);
        ALC11.alcCloseDevice(device);
    }
}
