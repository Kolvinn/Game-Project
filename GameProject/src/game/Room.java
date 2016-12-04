package game;

import java.awt.Point;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import game.Player.Direction;

/**
 * represents a room in the Game, a room is a 2 dimensional array of Tile objects with a String name
 * @author purvisjere
 *
 */
public class Room implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -2277257086949286812L;
	protected Tile[][] tiles;
	protected String roomName;
	protected boolean isDark = false;
	protected Set<Stairwell> stairs = new HashSet<Stairwell>();

	//some byte constants to make the tile-byte conversions more readable
	protected final byte nullByte = Byte.parseByte("0");
	protected final byte emptyTileByte = Byte.parseByte("1");
	protected final byte wallByte = Byte.parseByte("2");
	protected final byte stairByte = Byte.parseByte("3");
	protected final byte objectByte = Byte.parseByte("4");
	protected final byte northPlayerByte = Byte.parseByte("5");
	protected final byte eastPlayerByte = Byte.parseByte("6");
	protected final byte southPlayerByte = Byte.parseByte("7");
	protected final byte westPlayerByte = Byte.parseByte("8");
	protected final byte storageByte = Byte.parseByte("9");
	protected final byte stairBlockByte = Byte.parseByte("10");

	private HashMap<Point, WorldObject> worldObjects = new HashMap<Point, WorldObject>();



	public Room(String roomName, Tile[][] tiles, Set<Stairwell> stairs) {
		this.roomName = roomName;
		this.tiles = tiles;
		this.stairs=stairs;
	}

	/**
	 * return the underlying tiles[][] of this room
	 * IMPORTANT: please don't have have other classes keep a local copy of the array, call this method whenever you need it
	 * as, at least at the client end, this array can (and will) be overwritten
	 * @return
	 */
	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * return the witdth of the tile[][], namely the length of the first array, tiles.length
	 * @return
	 */
	public int getWidth(){
			return tiles.length;
	}

	/**
	 * set the worldObject passed in to be at the location specified by point x, y
	 * @param point
	 * @param object
	 */
	public void setWorldObject(Point point, WorldObject object){
		worldObjects.put(point, object);
	}

	/**
	 * get the point representing the x y coordiantes of the passed in WorldObject if they exist in this room, otherwise null
	 * @param object
	 * @return
	 */
	public Point getObjectPoint(WorldObject object){
		for(Entry<Point, WorldObject> entry: worldObjects.entrySet()){
			if(entry.getValue().equals(object))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * remove a worldObject from the room
	 * specifically doesnt require us to know the location of the object in the room
	 * @param object
	 */
	public void removeWorldObject(WorldObject object){
		worldObjects.remove(getObjectPoint(object), object);
	}

	/**
	 * get a worldobject(if any) at point
	 * @param point
	 * @return
	 */
	public WorldObject getWorldObject(Point point){
		return(worldObjects.get(point));
	}

	/**
	 * removes a a player at point from this room
	 * @param point
	 * @param player
	 */
	public void removeWorldObject(Point point, Player player) {
		worldObjects.remove(point, player);

	}
	public Collection<WorldObject> getWorldObjects(){
		return worldObjects.values();
	}

	/**
	 * returns the map from Point to world object that this room maintains
	 * @return
	 */
	public Map<Point, WorldObject> getMap(){
		return worldObjects;
	}

	/**
	 * get whether a given location is within the bounds of the Tile[][]
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean validLocation(int x, int y){
		if(x < 0 || y<0 || x>=tiles[0].length || y >= tiles.length){
			return false;
		}
		return true;
	}

	/**
	 *gets a stairtile object from a string staircode that represents it
	 * @param stairCode
	 * @return
	 */
	public Tile getStairTile(String stairCode){
		for(Tile t: stairs){
			if(t.toString().equals(stairCode))
				return t;
		}
		return null;
	}

	/**
	 * iterates over the room and returns the first Player found, obsolete except for testing purposes as it is overridden by the clientRoom subclass
	 * @return
	 */
	public Player getPlayer(){
		for(WorldObject ob :getWorldObjects()){
			if(ob.name.equals("bob"))
					return (Player) ob;
		}
		return null;
	}

	/**
	 * Returns a tile in this room at the specified coordinate. The method
	 * return null if no such tile exists.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile getTile(int x, int y) {
		if(this.validLocation(x, y)){
			return tiles[y][x];
		}
		return null;
	}
	/**
	 * returns this rooms name
	 */
	@Override
	public String toString() {
		return roomName;
	}

	public boolean equals(Object o) {
		if (o instanceof Room) {
			if (((Room) o).toString().equals(roomName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * return a byte[] representation of the tile[][] of this room, used to send the tiles[][] to the client
	 * @return
	 */
	public byte[] toByteArray(){
			byte[] bytes = new byte[(tiles.length) * (tiles[0].length+1)];
			int i, j, k=0;
			for (i=0; i<tiles.length;++i,++k){ //nested loops run through the tiles[i][j], k is position in bytes[] incremented in both for-loops
				for(j=0;j<tiles[0].length;++j,++k){
					Tile tile = tiles[i][j];
					bytes[k]=tileToByte(tile, j, i);
				}
			}
			return bytes;

	}

	/**
	 * get a byte representation of the passed in tile object
	 * @param tile
	 * @return
	 */
	private byte tileToByte(Tile tile, int x, int y){//series of if statements, less efficient as it performs more checks than the nested equivalent but more readable
		Point p = new Point(x,y);
		if(tile instanceof Stairwell){//check stairs before player, as teh player spawns on stairs, if i load player first the client will never realize the stairs exist
			if(worldObjects.get(p) instanceof StairBlock){
				return stairBlockByte;
			}
			return stairByte;
		}
		if(worldObjects.containsKey(p)){
			WorldObject object = worldObjects.get(p);
			if(Storage.class.isAssignableFrom(object.getClass())){
				if(object instanceof Player){
					Direction d = ((Player)object).getDirectionSet().lastDirection();
					switch(d){
					case North:
						return northPlayerByte;
					case East:
						return eastPlayerByte;
					case South:
						return southPlayerByte;
					case West:
						return westPlayerByte;
					}
				}
				return storageByte;
			}
			return objectByte;
		}
		if(tile == null){return nullByte;};
		if(tile.toString().equals("wa")){return wallByte;}
		if(tile.toString().equals("ul")){return wallByte;}
		return emptyTileByte;
	}

	/**
	 * return whether this room is dark, true for dark, false for light
	 * @return
	 */
	public boolean getIsDark(){
		return isDark;
	}

	/**
	 * set the isDark boolean, true if this room is unlit, false for lit
	 * @param dark
	 */
	public void setIsDark(boolean dark){
		this.isDark = dark;
	}

}

