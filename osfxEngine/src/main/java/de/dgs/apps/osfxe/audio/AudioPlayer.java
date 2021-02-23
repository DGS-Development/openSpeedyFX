package de.dgs.apps.osfxe.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

public class AudioPlayer {
	public enum PlayerState {
		PLAYING,
		PAUSED,
		STOPPED,
		CLOSED
	}

	private byte[] audioBytes;

	private Long frameId; 
	private Clip clip;
	
	private PlayerState playerState = PlayerState.STOPPED;

	private final boolean isAudioLooped;

	/**
	 * Creates a new audio player. The audio isn't looped. The default volume is set to 1.
	 * @param audioBytes The audio data in a supported format.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public AudioPlayer(byte[] audioBytes) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this(audioBytes, false);
	}

	/**
	 * Creates a new audio player. The default volume is set to 1.
	 * @param audioBytes The audio data in a supported format.
	 * @param loopAudio True if the audio should be looped.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public AudioPlayer(byte[] audioBytes, boolean loopAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this(audioBytes, loopAudio, 1);
	}

	/**
	 * Creates a new audio player.
	 * @param audioBytes The audio data in a supported format.
	 * @param loopAudio True if the audio should be looped.
	 * @param volume The default volume from 0 to 1.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public AudioPlayer(byte[] audioBytes, boolean loopAudio, float volume) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this(audioBytes, loopAudio, volume, null);
	}

	/**
	 * Creates a new audio player.
	 * @param audioBytes The audio data in a supported format.
	 * @param loopAudio True if the audio should be looped.
	 * @param volume The default volume from 0 to 1.
	 * @param lineListener {@link LineListener} to detect player changes.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public AudioPlayer(byte[] audioBytes, boolean loopAudio, float volume, LineListener lineListener) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this.audioBytes = audioBytes;
		isAudioLooped = loopAudio;

		setupAudio(createAudioInputStream(audioBytes), loopAudio);
		setVolume(volume);

		if(lineListener != null)
			clip.addLineListener(lineListener);
	}

	/**
	 * Returns the current player state.
	 * @return The active state.
	 */
	public PlayerState getPlayerState() {
		return playerState;
	}

	/**
	 * Returns weather the audio is looped or not.
	 * @return The loop condition.
	 */
	public boolean isAudioLooped() {
		return isAudioLooped;
	}

	/**
	 * Returns the set volume, from 0 to 1.
	 * @return The volume.
	 */
	public float getVolume() {
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    return (float) Math.pow(10f, gainControl.getValue() / 20f);
	}

	/**
	 * Sets the volume, from 0 to 1.
	 * @param volume The new volume.
	 * @throws IllegalArgumentException Exception if the volume has the wrong range.
	 */
	public void setVolume(float volume) throws IllegalArgumentException {
	    if (volume < 0f || volume > 1f)
	        throw new IllegalArgumentException("Invalid volume level set (valid range from 0 to 1): " + volume);
	    
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}

	/**
	 * Starts the player, unless it is playing or closed.
	 * @return Success of the operation.
	 */
	public boolean play() {
		if(playerState == PlayerState.PLAYING || playerState == PlayerState.CLOSED)
			return false;

		if(isAudioLooped)
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		clip.start(); 
		playerState = PlayerState.PLAYING;

		return true;
	}

	/**
	 * Pauses the player, unless it is stopped, already paused or closed.
	 * @return Success of the operation.
	 */
	public boolean pause() {
		if (playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED
				|| playerState == PlayerState.CLOSED)
			return false;
		
		frameId = clip.getMicrosecondPosition(); 
		clip.stop(); 
		
		playerState = PlayerState.PAUSED;
		return true;
	}

	/**
	 * Resumes the player, unless it is playing or closed.
	 * @return Success of the operation.
	 */
	public boolean resume() {
		if (playerState == PlayerState.PLAYING || playerState == PlayerState.CLOSED)
			return false;
		
		clip.setMicrosecondPosition(frameId);

		return play();
	}

	/**
	 * Restarts the player, by playing form the beginning, unless it is closed.
	 * @return Success of the operation.
	 */
	public boolean restart() {
		if(playerState == PlayerState.CLOSED)
			return false;

		stop();
		
		return play();
	}

	/**
	 * Stops the player, and resets the audio to the beginning, unless it is stopped or closed.
	 * @return Success of the operation.
	 */
	public boolean stop() {
		if(playerState == PlayerState.STOPPED || playerState == PlayerState.CLOSED)
			return false;

		clip.stop();

		frameId = 0L;
		clip.setMicrosecondPosition(0);
		
		playerState = PlayerState.STOPPED;
		return true;
	}

	/**
	 * Jumps to a certain audio position, unless the player is closed.
	 * @return Success of the operation.
	 */
	public boolean jump(long position) {
		if(playerState == PlayerState.CLOSED)
			return false;
		
		if (position >= 0 && position < clip.getMicrosecondLength()) {
			if(playerState != PlayerState.STOPPED)
				clip.stop();

			frameId = position;
			clip.setMicrosecondPosition(position); 
			
			return play();
		}
		else {
			return false;
		}
	}

	/**
	 * Reloads the player audio. Sets the status from closed to stopped.
	 * Keeps the current loop status.
	 * @return Success of the operation.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public boolean reloadAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		return reloadAudio(null, isAudioLooped);
	}

	/**
	 * Reloads the player audio. Sets the status from closed to stopped.
	 * Keeps the current loop status.
	 * @param newAudioBytes New audio data in a supported audio format.
	 * @return Success of the operation.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public boolean reloadAudio(byte[] newAudioBytes) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		return reloadAudio(newAudioBytes, isAudioLooped);
	}

	/**
	 * Reloads the player audio. Sets the status from closed to stopped.
	 * @param loopAudio True if the audio should be looped.
	 * @return Success of the operation.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public boolean reloadAudio(boolean loopAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		return reloadAudio(null, loopAudio);
	}

	/**
	 * Reloads the player audio. Sets the status from closed to stopped.
	 * @param newAudioBytes New audio data in a supported audio format.
	 * @param loopAudio True if the audio should be looped.
	 * @return Success of the operation.
	 * @throws UnsupportedAudioFileException If the audio format is not supported.
	 * @throws IOException Problems when opening the stream.
	 * @throws LineUnavailableException Problems when playing the stream.
	 */
	public boolean reloadAudio(byte[] newAudioBytes, boolean loopAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if(playerState != PlayerState.CLOSED)
			return false;

		AudioInputStream audioInputStream;

		if(newAudioBytes != null) {
			audioInputStream = createAudioInputStream(newAudioBytes);
		}
		else {
			audioInputStream = createAudioInputStream();
		}

		setupAudio(audioInputStream, loopAudio);
		playerState = PlayerState.STOPPED;

		return true;
	}

	/**
	 * Closes the player, which requires a reload, unless it is already closed.
	 * @return Success of the operation.
	 */
	public boolean close() {
		if(playerState == PlayerState.CLOSED)
			return false;

		stop();
		clip.close();
		playerState = PlayerState.CLOSED;

		return true;
	}

	private AudioFormat getOutputAudioFormat(AudioFormat formatBasis) {
        int channels = formatBasis.getChannels();
        float rate = formatBasis.getSampleRate();
        
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				rate,
				16,
				channels,
				channels * 2,
				rate,
				false);
    }

	private AudioInputStream createAudioInputStream() throws IOException, UnsupportedAudioFileException {
		return createAudioInputStream(audioBytes);
	}

	private AudioInputStream createAudioInputStream(byte[] newAudioBytes) throws IOException, UnsupportedAudioFileException {
		this.audioBytes = newAudioBytes;

		return AudioSystem.getAudioInputStream(
				new BufferedInputStream(new ByteArrayInputStream(newAudioBytes)));
	}

	private void setupAudio(AudioInputStream audioInputStream, boolean loopAudio) throws LineUnavailableException, IOException {
		AudioFormat targetAudioFormat = getOutputAudioFormat(audioInputStream.getFormat());

		clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(targetAudioFormat, audioInputStream));

		if(loopAudio)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
}

