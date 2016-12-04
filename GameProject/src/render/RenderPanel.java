package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.ClientRoom;
import game.GameRunner;
import game.Player;
import game.Tile;
import game.WorldObject;
import topLevel.LayeredPane;
import game.Player.Direction;
import game.Room;

/**
 * The renderer for the game. It takes in a 2d array of Tiles and then iterates
 * through it, displaying the tile itself and whatever is on it. The view is
 * naturally top-down but the sprites are overlapping, giving the game a '3d'
 * effect. The implementation of a rotate-able game board also gives the game a
 * stronger '3d' effect.
 *
 * @author zhengzhon
 *
 */
public class RenderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static int tileWidth = 64;
	private final static int tileHeight = 64; // 64 for standard, 48 for yolo
	private final static int objectHeight = (int) (tileHeight * 1.5);
	private final static int wallHeight = (int) (tileHeight * 2.5);

	private Player currentPlayer;
	private int playerX;
	private int playerY;

	private int viewDistance = 6;
	//private int playerPointX;
	//private int playerPointY;

	private Polygon torchBeamPoly = null;
	private Polygon darknessPoly = null;

	//private boolean isDark = true;

	private Map<Rectangle, String[]> tileItems = new HashMap<Rectangle, String[]>();

	private Map<String, BufferedImage> itemSprites = new HashMap<String, BufferedImage>();
	private Map<String, BufferedImage> tileSprites = new HashMap<String, BufferedImage>();
	private Map<String, BufferedImage> playerSprites = new HashMap<String, BufferedImage>();
	private boolean firstTime = true;
	private int fft=0;
	public enum Rotation {
		NORTH, EAST, SOUTH, WEST
	};

	private Room map;
	private Tile[][] tiles;

	private Rotation perspective;

	public Animation itemAnimation;

	/**
	 * The Renderer renders the game frame based on the map that is passed in.
	 * The initial perspective of the game is set to "NORTH".
	 *
	 * @param map
	 */
	public RenderPanel() {
		this.setSize(1000,1000);
		this.setVisible(true);
		//TODO CHANGE PLAYER CREATION
		GameRunner game = new GameRunner();
		Player player = game.createPlayer("bob","Mr Hero Pants");
		this.map = game.getPlayerRoom(player);
		this.perspective = Rotation.NORTH;

		loadupSprites();
		System.out.println("in the rendererRemade contrustor   \n"+ map.getMap());
		this.currentPlayer = player;
		//this.repaint();
		//this.playerPointX = currentPlayer.getPoint().x;
		//this.playerPointY = currentPlayer.getPoint().y;
	}

	/**
	 * Preloads up the sprites to save time when drawing the map
	 *
	 */
	private void loadupSprites(){
		int index =0;
		try{
		itemSprites.put("Chest",ImageIO.read(new File("src/sprites/Chest.png")));++index;
		itemSprites.put("Crystal",ImageIO.read(new File("src/sprites/Crystal.png")));++index;
		itemSprites.put("StairBlock",ImageIO.read(new File("src/sprites/StairLock4.png")));++index;
		itemSprites.put("Error",ImageIO.read(new File("src/sprites/Error1.png")));++index;
		itemSprites.put("FlashLight",ImageIO.read(new File("src/sprites/FlashLight.png")));++index;
		itemSprites.put("Kiosk",ImageIO.read(new File("src/sprites/Kiosk.png")));++index;
		itemSprites.put("Knife",ImageIO.read(new File("src/sprites/Knife.png")));++index;
		itemSprites.put("Ornament",ImageIO.read(new File("src/sprites/Ornament.png")));++index;
		itemSprites.put("Shoe",ImageIO.read(new File("src/sprites/Shoe.png")));++index;
		itemSprites.put("Sign Post",ImageIO.read(new File("src/sprites/Sign Post.png")));++index;
		itemSprites.put("Torch",ImageIO.read(new File("src/sprites/Torch.png")));++index;

		tileSprites.put("Error",ImageIO.read(new File("src/tiles/Error.png")));++index;
		tileSprites.put("fn",ImageIO.read(new File("src/tiles/fn.png")));++index;
		tileSprites.put("s1",ImageIO.read(new File("src/tiles/s1.png")));++index;
		tileSprites.put("wa",ImageIO.read(new File("src/tiles/wa.png")));++index;

		playerSprites.put("Player1NE",ImageIO.read(new File("src/players/Player1NE.png")));++index;
		playerSprites.put("Player1NW",ImageIO.read(new File("src/players/Player1NW.png")));++index;
		playerSprites.put("Player1SE",ImageIO.read(new File("src/players/Player1SE.png")));++index;
		playerSprites.put("Player1SW",ImageIO.read(new File("src/players/Player1SW.png")));++index;
		playerSprites.put("Player2NE",ImageIO.read(new File("src/players/Player2NE.png")));++index;
		playerSprites.put("Player2NW",ImageIO.read(new File("src/players/Player2NW.png")));++index;
		playerSprites.put("Player2SE",ImageIO.read(new File("src/players/Player2SE.png")));++index;
		playerSprites.put("Player2SW",ImageIO.read(new File("src/players/Player2SW.png")));++index;
		playerSprites.put("Player3NE",ImageIO.read(new File("src/players/Player3NE.png")));++index;
		playerSprites.put("Player3NW",ImageIO.read(new File("src/players/Player3NW.png")));++index;
		playerSprites.put("Player3SE",ImageIO.read(new File("src/players/Player3SE.png")));++index;
		playerSprites.put("Player3SW",ImageIO.read(new File("src/players/Player3SW.png")));++index;
		playerSprites.put("Player4NE",ImageIO.read(new File("src/players/Player4NE.png")));++index;
		playerSprites.put("Player4NW",ImageIO.read(new File("src/players/Player4NW.png")));++index;
		playerSprites.put("Player4SE",ImageIO.read(new File("src/players/Player4SE.png")));++index;
		playerSprites.put("Player4SW",ImageIO.read(new File("src/players/Player4SW.png")));++index;
		}
		catch(IOException e){
			System.err.println("Error: " + e.getLocalizedMessage()+" "+index);
		}
	}

	/**
	 * Orientate the flashlight's field of vision based on the direction the player
	 * is currently facing
	 *
	 * @param direction
	 * @return
	 * @author Jeremy Purvis 300252530
	 */
	private Polygon orientateTorch(Direction direction){
		int baseCooX =0, baseCooY=0;

		direction = orientateFlashLight(direction);
		int playerPointX =5;
		int playerPointY = 6;
		switch(direction){
		case East:
			baseCooX = playerPointX+1;
			baseCooY = playerPointY-1;

			return new Polygon(
					new int[]{(baseCooX-1)*tileWidth,baseCooX*tileWidth,(baseCooX+4)*tileWidth,(baseCooX+4)*tileWidth,baseCooX*tileWidth,(baseCooX-1)*tileWidth},
					new int[]{(baseCooY+1)*tileHeight,(baseCooY+1)*tileHeight,(baseCooY+2)*tileWidth,(baseCooY-1)*tileWidth,baseCooY*tileHeight,baseCooY*tileHeight},
					6);
		case North:
			baseCooX = playerPointX-1;
			baseCooY = playerPointY-4;

			return new Polygon(
					new int[]{baseCooX*tileWidth,(baseCooX+1)*tileWidth,(baseCooX+1)*tileWidth,(baseCooX+2)*tileWidth,(baseCooX+2)*tileWidth,(baseCooX+3)*tileWidth},
					new int[]{(baseCooY-1)*tileHeight,(baseCooY+3)*tileHeight,(baseCooY+4)*tileHeight,(baseCooY+4)*tileHeight,(baseCooY+3)*tileWidth,(baseCooY-1)*tileWidth},
					6);
		case South:
			baseCooX = playerPointX-1;
			baseCooY = playerPointY;

			return new Polygon(
					new int[]{baseCooX*tileWidth,(baseCooX+1)*tileWidth,(baseCooX+1)*tileWidth,(baseCooX+2)*tileWidth,(baseCooX+2)*tileWidth,(baseCooX+3)*tileWidth},
					new int[]{(baseCooY+4)*tileHeight,(baseCooY)*tileHeight,(baseCooY-1)*tileHeight,(baseCooY-1)*tileHeight,(baseCooY)*tileWidth,(baseCooY+4)*tileWidth},
					6);
		case West:
			baseCooX = playerPointX-3;
			baseCooY = playerPointY-2;

			return new Polygon(
					new int[]{(baseCooX-1)*tileWidth,(baseCooX+3)*tileWidth,(baseCooX+4)*tileWidth,(baseCooX+4)*tileWidth,(baseCooX+3)*tileWidth,(baseCooX-1)*tileWidth},
					new int[]{baseCooY*tileHeight,(baseCooY+1)*tileHeight,(baseCooY+1)*tileHeight,(baseCooY+2)*tileWidth,(baseCooY+2)*tileWidth,(baseCooY+3)*tileWidth},
					6);
		default:
			return null;

		}

	}

	/**
	 * Gets the field of view when the player is in a dark room without a flashlight on hand
	 *
	 * @return
	 */
	private Polygon setDarkvision() {
		int playerPointX =5;
		int playerPointY = 6;
		return new Polygon(

				new int[]{(playerPointX - 1) * tileWidth, playerPointX * tileWidth, (playerPointX + 1) * tileWidth, (playerPointX + 2) * tileWidth,
						(playerPointX + 2) * tileWidth, (playerPointX + 1) * tileWidth, playerPointX * tileWidth,  (playerPointX - 1) * tileWidth},
				new int[]{(playerPointY - 1) * tileHeight, (playerPointY - 2) * tileHeight, (playerPointY - 2) * tileHeight, (playerPointY - 1) * tileHeight,
						playerPointY * tileHeight, (playerPointY + 1) * tileHeight, (playerPointY + 1) * tileHeight, playerPointY * tileHeight},
				8);
	}

	/**
	 * The getItem method returns a BufferedImage of an image with the same name
	 * passed in the parameter
	 *
	 * @param tileItem
	 * @return
	 */
	private BufferedImage getItem(String tileItem) {
		BufferedImage image = itemSprites.get(tileItem);

		if(image != null)
			return image;
		else
			return getItem("Error");

	}

	/**
	 * Return the sprite assigned to the player and orientate them.
	 * Player 1 = Green
	 * Player 2 = Blue
	 * Player 3 = Red
	 * Player 4 = Brown
	 *
	 * @param player
	 * @return
	 */
	private BufferedImage getPlayer(Player player){

		/*
		 * return the sprite based on what direction the player was
		 * initially facing
		 */
		String vertical;
		String horizontal;

		// code to determine which player sprite to use
		Direction lastDirection = player.getDirectionSet().lastDirection();
		System.out.println(lastDirection);
		switch (lastDirection) {
		case North:
			player.setNorth(true);
			break;
		case South:
			player.setNorth(false);
			break;
		case East:
			player.setEast(true);
			break;
		case West:
			player.setEast(false);
			break;
		default:
			break;
		}

		switch (perspective) {
		case SOUTH:
			if (player.getNorthFace())
				vertical = "S";
			else
				vertical = "N";

			if (player.getEastFace())
				horizontal = "W";
			else
				horizontal = "E";
			break;
		case EAST:
			if (player.getNorthFace())
				horizontal = "W";
			else
				horizontal = "E";

			if (player.getEastFace())
				vertical = "N";
			else
				vertical = "S";
			break;
		case WEST:
			if (player.getNorthFace())
				horizontal = "E";
			else
				horizontal = "W";

			if (player.getEastFace())
				vertical = "S";
			else
				vertical = "N";
			break;
		default: //NORTH
			if (player.getNorthFace())
				vertical = "N";
			else
				vertical = "S";

			if (player.getEastFace())
				horizontal = "E";
			else
				horizontal = "W";
			break;
		}

		BufferedImage image = playerSprites.get("Player" + "1" + vertical + horizontal);

		if(image != null)
			return image;
		else
			return getItem("Error");
	}

	/**
	 * The getTile method returns a BufferedImage of a tile with the same name
	 * passed in the parameter
	 *
	 * @param tileItem
	 * @return
	 */
	private BufferedImage getTile(String tileItem) {
		BufferedImage image = tileSprites.get(tileItem);

		if(image != null)
			return image;
		else
			return getItem("Error");

	}

	public void paintComponent(Graphics g) {

		System.out.println("SDKNSDLNSLKDNLSDNLSNDLKSNDLKNSDLKNSLDNLSND");

			super.paintComponent(g);
			this.setBackground(Color.BLACK);
			tileItems.clear();

			// If is no room loaded up
			if (map.getTiles() == null || map.getTiles()[0][0]== null) {
				g.setColor(Color.BLUE);
				g.setFont(new Font("Arial", 0, 84));
				g.drawString("Room is null", 200, 250);
				return;
			}


			// Get/update the tiles array to render
			tiles = map.getTiles();

			// Gets the player's current position as a point of focus
			System.out.println(currentPlayer.getPoint());
			playerX = currentPlayer.getPoint().x;
			playerY = currentPlayer.getPoint().y;



			// Get/update the tiles array to render
			//for(Tile[] tiles: map.getTiles()){
				//for(Tile t: tiles){
				//	System.out.print(t.getObject());
				//}
				//System.out.println();
			//}
			tiles = map.getTiles();


			// Rotate the room
			switch (perspective) {
			case EAST:
				tiles = rotateArray(tiles);
			case SOUTH:
				tiles = rotateArray(tiles);
			case WEST:
				tiles = rotateArray(tiles);
			default:
				break;
			}

			// Stacks used to reverse the order of the floor map
			Stack<Rectangle> hitBox = new Stack<Rectangle>();
			Stack<String[]> descriptions = new Stack<String[]>();

			// The top left corner of the screen in tiles
			int tileX = 0;
			int tileY = 0;

			// The coordinates for drawing the map on the screen
			int yPos = 0;
			int xPos = 0;

			//creates a polygon that only contains points within a torch beam, orientated via players last direction
			torchBeamPoly = orientateTorch(currentPlayer.getDirectionSet().lastDirection());
			darknessPoly = setDarkvision();

			// to render the area above the 'north' walls
			if (playerY < viewDistance - 1)
				tileY -= playerY - viewDistance + 1;

			for (int y = playerY - viewDistance + 1; y < playerY + viewDistance + 1; y++) {
				if (y < 0 || y > tiles.length - 1) {
					continue;
				}

				// to render the area across the 'west' walls
				if (playerX < viewDistance - 1)
					tileX -= playerX - viewDistance + 1;

				for (int x = playerX - viewDistance + 1; x < playerX + viewDistance; x++) {
					if (x < 0 || x > tiles[0].length - 1) {
						continue;
					}

					Tile t = tiles[y][x];

					BufferedImage objectImage = null;
					BufferedImage tileImage = null;

					boolean isWall = false;
					// Check for stairs
					if (t.toString().startsWith("s") && t.toString().length() == 2)
						tileImage = getTile("s1");
					else
						tileImage = getTile(t.toString());

					// If the current tile is a wall
					if (t.toString().equals("wa")) {
						isWall = true;
					}
					WorldObject object = map.getWorldObject(new Point(x,y));
					if(object!=null){
						System.out.println(object);
						if(object instanceof Player){
							objectImage = getPlayer((Player) object);
						}
						else
							objectImage = getItem(object.toString());
					}
					//t.getObject()
					yPos = tileY * tileHeight;
					xPos = tileX * tileWidth;
					int imageHeight = tileHeight;

					// Alter the position that the wall will be drawn
					if (isWall) {
						yPos = (tileY - 1) * tileHeight - tileHeight / 2;
						imageHeight = wallHeight;
					}

					//Draw the tiles
					g.drawImage(tileImage, xPos, yPos, tileWidth, imageHeight, null);

					if (objectImage != null) {

						yPos = (tileY - 1) * tileHeight + tileHeight / 2;
						imageHeight = objectHeight;

						g.drawImage(objectImage, xPos, yPos, tileWidth, imageHeight, null);

						String itemName = "Unknown Object";
						String itemDescription = "No description available";

						// Add the tile to a map of tile coordinates to tile/item description
						itemName = object.toString();
						itemDescription = object.getDescription();

						String[] temp = new String[2];
						temp[0] = itemName;
						temp[1] = itemDescription;

						Rectangle r = new Rectangle(xPos, yPos + (tileHeight / 2), tileWidth, imageHeight);
						hitBox.push(r);

						descriptions.push(temp);
					}


					tileX++;
				}
				tileX = 0;

				tileY++;
			}

			/*
			 * The protagonist has a antenna on the top of their head which allows minimal light
			 * to be projected around them thus allowing a slight field of view. This can be
			 * enhanced with a flashlight pickup which allows a cone of vision to be emitted from
			 * the antenna. This cone of view is also special as it's electon beams contains an
			 * x-ray property that can illuminate tiles on the other side of the walls. However
			 * due to their high-energy emission, the light waves' lifespan is severely shortened
			 * therefore limiting the distance that the antenna can successfully light up.
			 */

			// If the current room is dark, limit the player's field of vision
			if(map.getIsDark()){

				Graphics2D g2d = (Graphics2D) g.create();
				Area outter = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
				//System.out.println(currentPlayer.getInventory()+"   "+currentPlayer.hasFlashlight());
				if(currentPlayer.hasFlashlight())
					outter.subtract(new Area(torchBeamPoly));
				outter.subtract(new Area(darknessPoly));

				g2d.setColor(Color.BLACK);

				g2d.fill(outter);
			}

			// Reverse the order of placing the objects to allow the items rendered in front to
			// be the first objects selectable by the mouseListener
			while (!hitBox.isEmpty()) {
				Rectangle r = hitBox.pop();
				String[] s = descriptions.pop();
				tileItems.put(r, s);
			}
		
	}

	/**
	 * Returns the string assigned to the specific tile through the coordinates
	 * passed into the method. It first iterates through the whole map of tiles
	 * and once it finds the coordinates that fit within the iterated tile, it
	 * returns the description assigned to the tile
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public String[] getTile(int x, int y) {
		for (Map.Entry<Rectangle, String[]> entry : tileItems.entrySet()) {

			/*
			 * The coords are initially stored as a string so to get the integer
			 * values the string requires to be split into individual strings
			 * then parsed as ints
			 */

			if (entry.getKey().contains(x, y))
				return entry.getValue();
		}
		return null;
	}

	/**
	 * Allows the flashlight to render the correct direction based on the rotation of the game
	 *
	 * @param direction
	 * @return
	 */
	private Direction orientateFlashLight(Direction direction){
		switch(perspective){
		case EAST:
			switch (direction) {
			case North:
				return Direction.West;
			case East:
				return Direction.North;
			case South:
				return Direction.East;
			case West:
				return Direction.South;
			default:
				break;
			}
		case SOUTH:
			switch (direction) {
			case North:
				return Direction.South;
			case East:
				return Direction.West;
			case South:
				return Direction.North;
			case West:
				return Direction.East;
			default:
				break;
			}
		case WEST:
			switch (direction) {
			case North:
				return Direction.East;
			case East:
				return Direction.South;
			case South:
				return Direction.West;
			case West:
				return Direction.North;
			default:
				break;
			}
		default:
			break;

		}
		return direction;
	}

	/**
	 * Sets the perspective of the board by rotating the viewpoint clockwise
	 *
	 */
	public Rotation rotatePerspective() {
		switch (perspective) {
		case NORTH:
			perspective = Rotation.EAST;
			break;
		case EAST:
			perspective = Rotation.SOUTH;
			break;
		case SOUTH:
			perspective = Rotation.WEST;
			break;
		case WEST:
			perspective = Rotation.NORTH;
			break;
		default:
			break;
		}
		return perspective;

	}

	/**
	 * Rotate the 2d array of tiles clockwise
	 *
	 * @param origin
	 * @return
	 */
	private Tile[][] rotateArray(Tile[][] origin) {

		int yMax = origin.length;
		int xMax = origin[0].length;
		int x = yMax;
		int y = xMax;

		Tile[][] rotated = new Tile[y][x];
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {

				rotated[i][j] = origin[x - j - 1][i];
				WorldObject obj = rotated[i][j].getObject();

				if (obj instanceof Player) {

					Point p = ((Player)obj).getPoint();
					if(p.equals(map.getPlayer().getPoint())){
						playerX = j;
						playerY = i;

					}
				}
			}
		}
		return rotated;

	}

}
