package render;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The animation class that involves the timing and speed of the selection of
 * sprite sections of the sprite sheet
 *
 *
 * @author zhengzhon
 *
 */
public class Animation {

	private int frameCount;
	private int frameDelay;
	private int currentFrame;
	private int animationDirection;
	private int totalFrames;

	private boolean stopped;

	private List<Frame> theFrames = new ArrayList<Frame>();

	/**
	 * Creation of an animation object that takes in an array of processed
	 * single-frame sprites and a delay that determines the time between each
	 * frame
	 *
	 * @param frames
	 * @param delay
	 */
	public Animation(BufferedImage[] frames, int delay) {

		this.frameDelay = delay;
		this.stopped = true;

		for (int i = 0; i < frames.length; i++)
			addFrame(frames[i], frameDelay);

		this.frameCount = 0;
		this.frameDelay = delay;
		this.currentFrame = 0;
		this.animationDirection = 1;
		this.totalFrames = this.theFrames.size();

	}

	/**
	 * Adds the specific frame and assigned duration to the arraylist of all the
	 * frames
	 *
	 * @param frame
	 * @param duration
	 */
	public void addFrame(BufferedImage frame, int duration) {

		if (duration < 0)
			throw new RuntimeException("Invalid duration: " + duration);

		theFrames.add(new Frame(frame, duration));
		currentFrame = 0;

	}

	/**
	 * Gets the current frame of the sprite
	 *
	 * @return
	 */
	public BufferedImage getSprite() {
		return theFrames.get(currentFrame).getFrame();

	}

	/**
	 * Updates the current frame as the next one
	 *
	 */
	public void update() {

		if (!stopped)
			frameCount++;

		if (frameCount > frameDelay) {

			frameCount = 0;
			currentFrame += animationDirection;

			if (currentFrame > totalFrames - 1)
				currentFrame = 0;
			else if (currentFrame < 0)
				currentFrame = totalFrames - 1;

		}

	}

	/**
	 * Begins the animation process
	 *
	 */
	public void start() {

		if (!stopped)
			return;

		if (theFrames.size() < 1)
			return;

		stopped = false;

	}

	/**
	 * Stops the animation process
	 *
	 */
	public void stop() {

		if (theFrames.size() < 1)
			return;

		stopped = true;

	}

	/**
	 * Restarts the animation process from the beginning
	 *
	 */
	public void restart() {

		if (theFrames.size() < 0)
			return;

		stopped = false;
		currentFrame = 0;
	}

	/**
	 * Resets the animation object
	 *
	 */
	public void reset() {

		stopped = true;
		frameCount = 0;
		currentFrame = 0;

	}
}
