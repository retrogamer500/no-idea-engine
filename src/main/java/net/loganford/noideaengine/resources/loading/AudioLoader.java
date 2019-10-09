package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.audio.Audio;
import net.loganford.noideaengine.config.json.AudioConfig;
import net.loganford.noideaengine.utils.file.DataSource;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class AudioLoader extends ResourceLoader {

    private List<AudioConfig> audioToLoad;

    public AudioLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getAudioManager().unloadGroups(ctx);
        audioToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getAudio() != null) {
            audioToLoad.addAll(game.getConfig().getResources().getAudio()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        AudioConfig config = audioToLoad.remove(0);
        Audio audio = load(config);
        populateResource(audio, config);
        game.getAudioManager().put(config.getKey(), audio);
        log.info("Audio loaded: " + config.getFilename());
    }

    @Override
    public int getRemaining() {
        return audioToLoad.size();
    }

    public Audio load(AudioConfig audioConfig) {
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer sampleRateBuffer = BufferUtils.createIntBuffer(1);

        DataSource location = audioConfig.getResourceMapper().get((audioConfig.getFilename()));
        ShortBuffer audioBuffer = STBVorbis.stb_vorbis_decode_memory(location.loadBytes(), channelsBuffer, sampleRateBuffer);

        if(audioBuffer == null) {
            throw new GameEngineException("Could not process audio file");
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        int format = -1;
        if(channels == 1) {
            format = AL11.AL_FORMAT_MONO16;
        } else if(channels == 2) {
            format = AL11.AL_FORMAT_STEREO16;
        }

        int bufferId = AL11.alGenBuffers();
        AL11.alBufferData(bufferId, format, audioBuffer, sampleRate);

        Audio audio = new Audio(getGame().getAudioSystem(), bufferId, sampleRate, audioBuffer.limit());
        MemoryUtil.memFree(audioBuffer);
        return audio;
    }
}
