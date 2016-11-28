package render;

import java.awt.image.BufferedImage;

/**
 * The sprite frame class. It stores a specific frame of a sprite
 * and the duration it should appear for
 *
 * @author zhengzhon
 *
 */
public class Frame {

	private BufferedImage theFrame;
	private int theDuration;

	/**
	 * Create a frame object with an image of the frame and the
	 * specified duration
	 *
	 * @param frame
	 * @param duration
	 */
	public Frame(BufferedImage frame, int duration){
		theFrame = frame;
		theDuration = duration;

	}

	/**
	 * Get the image of the frame
	 *
	 * @return
	 */
	public BufferedImage getFrame(){
		return theFrame;

	}

	/**
	 * Change the frame's image
	 *
	 * @param frame
	 */
	public void setFrame(BufferedImage frame){
		theFrame = frame;

	}

	/**
	 * Get the duration that the frame is to appear for
	 *
	 * @return
	 */
	public int getDuration(){
		return theDuration;

	}

	/**
	 * Change the duration that the frame is to appear for
	 *
	 * @return
	 */
	public void setDuration(int duration){
		theDuration = duration;

	}

}
