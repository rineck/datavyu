package org.datavyu.plugins.ffmpegplayer;

import java.awt.event.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaPlayerWindow;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.SdlKeyEventListener;
import org.datavyu.plugins.ffmpeg.FfmpegSdlMediaPlayer;

import java.io.File;

public class FFmpegPlayer implements SdlKeyEventListener {

  	/** The logger for this class */
  	private static Logger logger = LogManager.getFormatterLogger(FFmpegPlayer.class);
	
	/** The movie stream for this movie player */
	private MediaPlayerWindow mediaPlayer;

	private FFmpegStreamViewer viewer;

	/**
	 * Construct an FFmpegPlayer by creating the underlying movie stream provider
	 * and registering stream listeners for the video and audio. The stream
	 * listener for the video will show the image in this JPanel.
	 *
	 * @param viewer The ffmpeg viewer
     * @param sourceFile The source file
	 */
	FFmpegPlayer(FFmpegStreamViewer viewer, File sourceFile) {
		try {
			this.viewer = viewer;
			this.mediaPlayer = new FfmpegSdlMediaPlayer(sourceFile.toURI());
			this.mediaPlayer.init();
			this.mediaPlayer.addSdlKeyEventListener(this);
		}catch (Exception e) {
			logger.error("Cannot initialize ffmpeg player due to error: ", e);
		}
	}

	/**
	 * Get the duration of the opened video/audio stream of this player in seconds.
	 *
	 * @return Duration of the opened stream.
	 */
	public double getDuration() {	return mediaPlayer.getDuration();	}

	/**
	 * Get the current time in seconds.
	 *
	 * @return Current time in seconds.
	 */
	public double getCurrentTime() { return mediaPlayer.getPresentationTime(); }

	/**
	 * Seek to the position.
	 *
	 * @param position Position in seconds.
	 */
	public void setCurrentTime(double position) { mediaPlayer.seek(position); }

	/**
	 * Clean up the player before closing.
	 */
	public void cleanUp() {
		this.mediaPlayer.dispose();
		this.mediaPlayer.removeSdlKeyEventListener(this);
	}

	/**
	 * Set the start back speed for this player.
	 *
	 * @param playbackSpeed The start back speed.
	 */
	public void setPlaybackSpeed(float playbackSpeed) { mediaPlayer.setRate(playbackSpeed); }

	/**
	 * Play the video/audio.
	 */
	public void play() { mediaPlayer.play(); }

	/**
	 * Stop the video/audio.
	 */
	public void stop() { mediaPlayer.stop(); }

	/**
	 * Pause the video/audio.
	 */
	public void pause() {	mediaPlayer.pause(); }

	@Deprecated
	public void setScale(float scale) {
		// TODO(fraudies): Implement me
  }

	/**
	 * Instead of isPlaying a sequence of frames just step by one frame.
	 */
	public void stepForward() { mediaPlayer.stepForward(); }

	public void stepBackward() { mediaPlayer.stepBackward(); }

	/**
	 * Set the audio volume.
	 *
	 * @param volume New volume to set.
	 */
	public void setVolume(float volume) {	mediaPlayer.setVolume(volume); }

	public void setMute(final boolean newMute) { mediaPlayer.setMute(newMute); }

	public boolean isMute() { return mediaPlayer.getMute(); }

	boolean isPlaying() { return mediaPlayer.getState() == PlayerStateEvent.PlayerState.PLAYING; }

  	public double getFPS() { return mediaPlayer.getFps();	}

	public void setViewerVisible(final boolean isVisible) {
		if (isVisible) {
			mediaPlayer.showWindow();
		} else {
			mediaPlayer.hideWindow();
		}
	}

	public int getImageWidth() {
		return mediaPlayer.getImageWidth();
	}

	public int getImageHeight() {
		return mediaPlayer.getImageHeight();
	}

	public void setViewerSize(final int width, final int height) {
		mediaPlayer.setWindowSize(width, height);
	}

	@Override
	public void onKeyEvent(Object source, long nativeMediaRef, int javaKeyCode) {
		logger.debug("Player dispatching event " + javaKeyCode);
		if (viewer != null) {
			viewer.dispatchEvent(new KeyEvent(
					viewer,
					KeyEvent.KEY_PRESSED,
					System.currentTimeMillis(),
					0,
					javaKeyCode,
					KeyEvent.CHAR_UNDEFINED,
					KeyEvent.KEY_LOCATION_NUMPAD));
		}
	}
}
