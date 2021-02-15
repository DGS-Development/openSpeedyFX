package de.dgs.apps.osfxe.audio;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Utility class specialized for sound-playback.
 */
public class SoundAudioPlayer {
    /**
     * Sound container to store a sound level and to interact with the {@link SoundAudioPlayer}.
     */
    public static class Sound {
        private final AudioPlayer simpleAudioPlayer;
        private float soundVolume;

        private Sound(byte[] audioBytes, float soundVolume, float playerVolume) throws IOException,
                LineUnavailableException, UnsupportedAudioFileException {
            simpleAudioPlayer = new AudioPlayer(audioBytes, false, playerVolume);
            this.soundVolume = soundVolume;
        }

        private AudioPlayer getSimpleAudioPlayer() {
            return simpleAudioPlayer;
        }

        public float getSoundVolume() {
            return soundVolume;
        }

        private void setSoundVolume(float soundVolume) {
            this.soundVolume = soundVolume;
        }
    }

    private final List<Sound> sounds;
    private float baseVolume;

    private final Random random;

    /**
     * Creates a new sound player, with a custom base volume for sounds.
     * @param baseVolume The base volume, from 0 to 1.
     */
    public SoundAudioPlayer(float baseVolume) {
        this(baseVolume, new Random());
    }

    /**
     * Creates a new sound player, with a custom base volume for sounds.
     * @param baseVolume The base volume, from 0 to 1.
     * @param random The {@link Random} used to play a random sound effect.
     */
    public SoundAudioPlayer(float baseVolume, Random random) {
        this.baseVolume = baseVolume;
        this.random = random;

        sounds = new LinkedList<>();
    }

    /**
     * Creates a new sound player, with a base volume of 1 for sounds.
     */
    public SoundAudioPlayer() {
        this(1);
    }

    /**
     * Adds a new sound to the player.
     * The additional sound volume is calculated by "baseVolume + ((1 - baseVolume) * soundVolume)".
     * @param audioBytes The sound data in a supported format.
     * @param additionalSoundVolume The sound volume above the default sound level, from 0 to 1.
     * @return The created {@link Sound}, added to the player.
     * @throws UnsupportedAudioFileException If the audio format is not supported.
     * @throws IOException Problems when opening the stream.
     * @throws LineUnavailableException Problems when playing the stream.
     */
    public Sound addSound(byte[] audioBytes, float additionalSoundVolume) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Sound sound = new Sound(audioBytes, additionalSoundVolume, calculateSoundVolume(additionalSoundVolume));
        sounds.add(sound);

        return sound;
    }

    /**
     * Adds a new sound to the player. The additional volume is set to 0.
     * @param audioBytes The sound data in a supported format.
     * @return The created {@link Sound}, added to the player.
     * @throws UnsupportedAudioFileException If the audio format is not supported.
     * @throws IOException Problems when opening the stream.
     * @throws LineUnavailableException Problems when playing the stream.
     */
    public Sound addSound(byte[] audioBytes) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        return addSound(audioBytes, 0);
    }

    /**
     * Helper function to add a new sound to the player, read from an input stream.
     * The additional sound volume is calculated by "baseVolume + ((1 - baseVolume) * soundVolume)".
     * @param inputStream The sound data stream in a supported format.
     * @param additionalSoundVolume The sound volume above the default sound level, from 0 to 1.
     * @return The created {@link Sound}, added to the player.
     * @throws UnsupportedAudioFileException If the audio format is not supported.
     * @throws IOException Problems when opening the stream.
     * @throws LineUnavailableException Problems when playing the stream.
     */
    public Sound addSound(InputStream inputStream, float additionalSoundVolume) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        return addSound(inputStream.readAllBytes(), additionalSoundVolume);
    }

    /**
     * Helper function to add a new sound to the player, read from an input stream.
     * The additional volume is set to 0.
     * @param inputStream The sound data stream in a supported format.
     * @return The created {@link Sound}, added to the player.
     * @throws UnsupportedAudioFileException If the audio format is not supported.
     * @throws IOException Problems when opening the stream.
     * @throws LineUnavailableException Problems when playing the stream.
     */
    public Sound addSound(InputStream inputStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        return addSound(inputStream, 0);
    }

    /**
     * Stops and removes a sound from the player.
     * @param sound The sound to remove.
     * @return Success if the sound was removed.
     */
    public boolean removeSound(Sound sound) {
        sound.getSimpleAudioPlayer().stop();
        sound.getSimpleAudioPlayer().close();

        return sounds.remove(sound);
    }

    /**
     * Returns the base volume of the player.
     * @return The current base volume.
     */
    public float getBaseVolume() {
        return baseVolume;
    }

    /**
     * Sets the base volume of the player, and updates all sounds.
     * @param baseVolume The new base volume.
     */
    public void setBaseVolume(float baseVolume) {
        this.baseVolume = baseVolume;

        for(Sound tmpSound : sounds)
            tmpSound.getSimpleAudioPlayer().setVolume(calculateSoundVolume(tmpSound.getSoundVolume()));
    }

    /**
     * Returns the volume above the base level for a certain sound.
     * @param sound The sound to read.
     * @return The volume set for the sound.
     */
    public float getSoundVolume(Sound sound) {
        return sound.getSoundVolume();
    }

    /**
     * Sets the volume above the base level for a certain sound.
     * The additional sound volume is calculated by "baseVolume + ((1 - baseVolume) * soundVolume)".
     * @param sound The sound to update.
     * @param soundVolume The new volume above the base level.
     */
    public void setSoundVolume(Sound sound, float soundVolume) {
        sound.setSoundVolume(soundVolume);
        sound.getSimpleAudioPlayer().setVolume(calculateSoundVolume(soundVolume));
    }

    /**
     * Stops all sounds currently playing.
     */
    public void stopSounds() {
        for(Sound tmpSound : sounds)
            tmpSound.getSimpleAudioPlayer().stop();
    }

    /**
     * Stops a certain sound from playing.
     * @param sound The sound to stop.
     */
    public void stopSound(Sound sound) {
        sound.getSimpleAudioPlayer().stop();
    }

    /**
     * Plays a stopped sound or restarts it while playing.
     * @param sound The sound to (re)start.
     */
    public void playSound(Sound sound) {
        if(sound.getSimpleAudioPlayer().getPlayerState() == AudioPlayer.PlayerState.PLAYING) {
            sound.getSimpleAudioPlayer().restart();
        }
        else {
            sound.getSimpleAudioPlayer().play();
        }
    }

    /**
     * Helper function to play a random sound.
     */
    public void playRandomSound() {
        int soundIndex = random.nextInt(sounds.size());
        playSound(sounds.get(soundIndex));
    }

    private float calculateSoundVolume(float soundVolume) {
        float leftVolume = (1 - baseVolume);

        if(leftVolume == 0)
            return  1;

        return baseVolume + (leftVolume * soundVolume);
    }
}
