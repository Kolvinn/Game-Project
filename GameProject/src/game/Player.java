package game;

import java.awt.Point;
import java.io.Serializable;

/**
 * The player class is just a representation of storage that can move around and know where it is coordinate and direction wise.
 * Every movement a player takes will update the direction it is facing. Apart from this the player behaves exactly like a storage
 * and world object.
 * @author Jeremy
 *
 */
public class Player extends Storage implements Serializable{
	public static enum Direction{
		North, South, East, West;
	}

	private static final long serialVersionUID = -8687304284901741022L;
	private DirectionSet directionSet;
	private Point point = new Point(-1,-1);//default point is out of bounds to avoid race conditions causing a player to spawn somewhere unintended
	private int crystalCount=0;
	private boolean northFace = true;
	private boolean eastFace = true;
	private int playerIndex;

	private boolean hasFlashLight = false;

	/**
	 * player constructor that takes a string name, a string description, an int governing its max number
	 * of items carried, and another int that got added by ryan and we have no idea what it does
	 * @param name
	 * @param description
	 * @param storageLimit
	 * @param number
	 */
	public Player(String name, String description, int storageLimit) {
		super(name, description, storageLimit);
		directionSet=new DirectionSet();
	}
	/**
	 * Returns the direction set for this player which contains all direction information relative to how
	 * the player is facing
	 */
	public DirectionSet getDirectionSet(){
		return directionSet;
	}

	@Override
	public void addItem(WorldObject item) throws GameError{
		if(item.toString().equals("FlashLight")){
			this.hasFlashLight=true;
		}
		super.addItem(item);
	}
	public void setFlashLight(boolean bool){
		this.hasFlashLight=bool;
	}

	@Override
	public void removeItem(WorldObject item) {
		if(item.toString().equals("FlashLight")){
			System.out.println("removeing");
			this.hasFlashLight=false;
		}
		super.removeItem(item);
	}

	/**
	 * Returns the index of the player
	 *
	 * @return
	 */
	public int getPlayerIndex(){
		return this.playerIndex;
	}
	/**
	 * Returns the Point object this player holds in relation to its position
	 */
	public Point getPoint(){
		return point;
	}
	/**
	 * Sets the Point object this player holds in relation to its position
	 * @param Point
	 */
	public void setPoint(Point point){
		this.point=point;

	}
	/**
	 * Sets the amount of crystals this player holds
	 * @param count
	 */
	public void setCrystalCount(int count){
		crystalCount=count;
	}
	public void resetCrystals(){
		WorldObject ob = new WorldObject("Crystal","asd");
		while(this.getInventory().contains(ob))
			this.getInventory().remove(ob);
		crystalCount=0;
	}
	public int crystalCount(){
		return crystalCount;
	}

	/**
	 * Sets the last Direction that this player hasMoved in, i.e the facing direction
	 * @param direction
	 */
	public void setLastDirection(Direction direction) {
		directionSet.setLastDirection(direction);
	}
	/**
	 * Sets whether or not the player is facing north
	 * @param b
	 */
	public void setNorth(boolean b){
		this.northFace = b;
	}
	/**
	 * Sets whether or not the player is facing east
	 * @param b
	 */
	public void setEast(boolean b){
		this.eastFace = b;
	}
	/**
	 * Return if the player is facing north
	 * @return
	 */
	public boolean getNorthFace(){
		return this.northFace;
	}
	/**
	 * Return if the player is facing east
	 * @return
	 */
	public boolean getEastFace(){
		return this.eastFace;
	}

	public boolean hasFlashlight(){
		return hasFlashLight;
	}
	/**
	 * governs player facing
	 * @author jeremy
	 *
	 */
	public class DirectionSet {

		private Direction up = Direction.North, down = Direction.South, left = Direction.West, right = Direction.East;
		private Direction lastDirection = Direction.North;

		/**
		 * rotate this players orientation so that if facing east, up() returns east as being forward, left is north etc
		 * @param direction
		 */
		public void orientate(Direction direction){
			switch(direction){
			case East:
				up = Direction.East;
				down = Direction.West;
				left = Direction.North;
				right = Direction.South;
				break;
			case North:
				up = Direction.North;
				down = Direction.South;
				left = Direction.West;
				right = Direction.East;
				break;
			case South:
				up = Direction.South;
				down = Direction.North;
				left = Direction.East;
				right = Direction.West;
				break;
			case West:
				up = Direction.West;
				down = Direction.East;
				left = Direction.South;
				right = Direction.North;
				break;

			}
		}
		/**
		 * set the direction this player is facing, usually the direction of last movement
		 * @param direction
		 */
		public void setLastDirection(Direction direction){
			this.lastDirection=direction;
			orientate(lastDirection);
		}

		/**
		 * get the direction the player is facing
		 * @return
		 */
		public Direction lastDirection(){
			return lastDirection;
		}

		/**
		 * return the direction that corresponds to forward for the players current orientation
		 * @return
		 */
		public Direction up(){
			return up;
		}

		/**
		 * return the direction that corresponds to backwards for the players current orientation
		 * @return
		 */
		public Direction down(){
			return down;
		}

		/**
		 * return the direction that corresponds to left for the players current orientation
		 * @return
		 */
		public Direction left(){
			return left;
		}

		/**
		 * return the direction that corresponds to right for the players current orientation
		 * @return
		 */
		public Direction right(){
			return right;
		}
	}
}
