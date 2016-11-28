package render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The sprite class that loads a subsection of a png image. The sprite sheet
 * will first be loaded up and then a snippet of the sheet will be returned
 * using getSprite
 *
 * @author zhengzhon
 *
 */
public class Sprite {

	public static BufferedImage theSprite;
	public static final int imageWidth = 64;
	public static final int imageHeight = (int) (imageWidth * 1.5);

	/**
	 * Loads up the image sprite sheet with the file name
	 *
	 * @param file
	 * @return
	 */
	public static BufferedImage loadSprite(String file){

		BufferedImage sprite = null;

		try{
			sprite = ImageIO.read(new File("src/animation/" + file + ".png"));

		}
		catch(IOException e){
			System.out.println("Error encountered: " + e.getMessage());
			System.out.println(file + " not found!");
		}

		return sprite;

	}

	/**
	 * Gets the sprite of the image and returns the specified section from the x
	 * and y coordinates
	 *
	 * @param sprite
	 * @param x
	 * @param y
	 * @return
	 */
	public static BufferedImage getSprite(String sprite, int x, int y){

		if(theSprite == null)
			theSprite = loadSprite(sprite);

		return theSprite.getSubimage(x * imageWidth, y * imageHeight, imageWidth, imageHeight);

	}
}
