package game;


import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;

import game.Player.Direction;
/**
 *the controller of the games model-view-controller, all actions by all connected clients are checked on the server side by methods in here before the are (or arn't) allowed
 *to execute on the view(client) side
 * @author Jeremy
 *
 */
public class GameRunner {

	Map<Room, Set<Player>> roomToPlayers = new HashMap<Room, Set<Player>>();
	Map<String, Room> roomNames = new HashMap<String, Room>();
	GameBuilder gameBuilder; //keep a reference to the Game incase we need it later

	private static final int PlayerStorage = 10;

	public GameRunner(){
		gameBuilder = new GameBuilder(roomToPlayers, roomNames);
	}
	/**
	 * Translates any direction adjacent to the player into coordinate increments in x and y.
	 * @param player
	 * @param direction
	 * @return
	 */
	public Point getCorrespondingPoint(Player player, game.Player.Direction direction){

		int x = player.getPoint().x, y= player.getPoint().y;
		switch (direction){
		case East:
			return new Point(x+1,y);
		case North:
			return new Point(x,y-1);
		case South:
			return new Point(x,y+1);
		case West:
			return new Point(x-1,y);
		default:
			return null;
		}
	}

	/**
	 * get the set of all players that are currently in the passed in room
	 * @param room
	 * @return
	 */
	public Set<Player> getPlayersInRoom(Room room){
		return roomToPlayers.get(room);
	}

	/**
	 * creates a new player object, adds it to the starting room, and returns that player object
	 * @param name
	 * @param discription
	 * @return
	 *
	 */
	public Player createPlayer(String name, String description){

		Room room = roomNames.get("room0");//get room 0
		Point point = getSpawnPoint(room);//get a spawn point
		System.out.println(name+"   "+description+"    "+PlayerStorage+"    ");
		Player player = new Player(name,description, PlayerStorage);
		player.setPoint(point);

		room.setWorldObject(point, player);
		
		getPlayersInRoom(room).add(player);
		System.out.println();
		return player;
	}




	/**
	 * This method determines the tile in front of the player, the way the player is facing, and will
	 * return the object in front of them that they interact with (because a player can only interact with an object they are in front of).
	 * The method will return null if either no such object exists or the object in front of them cannot be picked up.
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public WorldObject interact(Player player) throws GameError{

		Room room = getPlayerRoom(player);
		Point inFront = getCorrespondingPoint(player, player.getDirectionSet().up());//get the point infront of the player where the object we are interacting with should be

		WorldObject object =room.getWorldObject(inFront);
		//pickup if is an item
		if(object instanceof Item)	{

			if (((Item)object).isPickupable()){//pick up object case
				if(player.hasFlashlight() && object.toString().equals("FlashLight"))
					throw new GameError("You already have a flashlight, don't be greedy!");
				player.addItem((Item) object);
				room.removeWorldObject(object);;//as we have picked the object up we need to remove it from the room
				if(object.toString().equals("Crystal"))
					player.setCrystalCount(player.crystalCount()+1);//if object is a crystal we keep track of how many we have
				return object;
			}

		}
		else if(object instanceof StairBlock){//unlock stairs case
			if(((StairBlock) object).crystalLimit()==player.crystalCount()){//check we have enough crystals
				room.removeWorldObject(object);
				player.resetCrystals();
				return object;
				}
			else
				throw new GameError("You do not have the required amount of crystals!");
			}

		return null;//return null if we did not pickup an object or unlock a door
	}

	/**
	 * Removes the specified item from the players inventory and sets it on first available empty tile
	 * @param player
	 * @param item
	 */
	public Point dropObject(Player player, Item item)throws GameError{
		Room room = getPlayerRoom(player);
		Point dropPoint = dropPoint(player, room);//get the point we will drop the item at
		if(dropPoint!=null){//if we actually find an empty space next to us then remove the item, add it to the location specified by point and return that point
			player.removeItem(item);
			if(item.toString().equals("FlashLight"))
				player.setFlashLight(false);
			room.setWorldObject(dropPoint,item);
			return dropPoint;
		}
		throw new GameError("No space to drop item!");//otherwise throw a gameError to be handled by the server and output at the client end
	}
	/**
	 * DropTile finds the first available Tile that is free from objects and is able to have a world object on it. It will return
	 * this tile if it meets the conditions, otherwise it will return null.
	 * @param player
	 */
	public Point dropPoint(Player player, Room room){
		if(canDrop(getCorrespondingPoint(player, player.getDirectionSet().up()), room))//check through each direction in turn and return the first empty location found
			return getCorrespondingPoint(player, player.getDirectionSet().up());

		if(canDrop(getCorrespondingPoint(player, player.getDirectionSet().right()), room))
			return getCorrespondingPoint(player, player.getDirectionSet().right());

		if(canDrop(getCorrespondingPoint(player, player.getDirectionSet().left()), room))
			return getCorrespondingPoint(player, player.getDirectionSet().left());

		if(canDrop(getCorrespondingPoint(player, player.getDirectionSet().down()), room))
			return getCorrespondingPoint(player, player.getDirectionSet().down());

		return null;
	}
	/**
	 * Helper method that determines if an object can be dropped on this tile. The tile must be Walkable and
	 * have no other object on it
	 * @param tile
	 * @return
	 */
	private boolean canDrop(Point point, Room room){
		if(room.getTile(point.x,point.y).isWalkable() && room.getWorldObject(point)==null)
			return true;
		return false;
	}


	/**
	 * Returns the Room with the given name, or null if no such room exists
	 * @param name
	 * @return
	 */
	public Room getRoom(String name){
		return roomNames.get(name);
	}
	/**
	 * Returns the room to which the given player is mapped, or null if the player or room does not exist
	 * @param player
	 * @return
	 */
	public Room getPlayerRoom(Player player){
		for(Map.Entry<Room, Set<Player>> entry : roomToPlayers.entrySet()){
			if(entry.getValue().contains(player))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Returns true if the move in the specified direction is a valid move
	 * i.e it has no other world object occupying the square. Otherwise it will return false.
	 * The method will always update the players last Direction so that it can be drawn as such on the board, even if
	 * the player never moves from square to square.
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean movePlayer(Player player, Player.Direction direction){

		Room room = getPlayerRoom(player);
		Point current = player.getPoint();


		Point destinationPoint = getCorrespondingPoint(player, direction);
		Tile destTile = room.getTile((int)destinationPoint.getX(), (int)destinationPoint.getY());

			player.setLastDirection(direction);


		if(room.getWorldObject(destinationPoint)==null &&  destTile.isWalkable()){
			room.removeWorldObject(current,player);

			//must update rooms to reflect player leaving a room
			if(destTile instanceof Stairwell){
				Stairwell stair = (Stairwell) destTile;
				destTile = roomNames.get(stair.connectedRoom()).getStairTile(stair.connectedTile());

				roomToPlayers.get(room).remove(player);

				room = roomNames.get(stair.connectedRoom());

				destTile = room.getStairTile(stair.connectedTile());
				destinationPoint = new Point(destTile.getX(), destTile.getY());
				roomToPlayers.get(room).add(player);

			}

			room.setWorldObject(destinationPoint, player);
			player.setPoint(destinationPoint);

			return true;

		}
		else
			return false;
	}


	/**
	 * Returns the tile in the given room which is the next best suited for a spawn position for a player.
	 * It will skip over any tiles that already contains a world object.
	 * @param room
	 * @return
	 */
	public Point getSpawnPoint(Room room){
		Tile[][] tiles = room.getTiles();
		int midX = tiles[0].length/2;
		int midY = tiles.length/2;

		//this makes sure the spawn point is valid for this room by checking each tile and returning the first instance that does not have an object
		for(Map.Entry<Point, WorldObject> ob: room.getMap().entrySet())
		if(room.getWorldObject(new Point(midX, midY))==null)
			return new Point(midX,midY);
		if(room.getWorldObject(new Point(midX-1, midY))==null)
			return new Point(midX-1,midY);
		if(room.getWorldObject(new Point(midX, midY+1))==null)
			return new Point(midX,midY+1);
		if(room.getWorldObject(new Point(midX-1, midY+1))==null)
			return new Point(midX-1,midY+1);
		if(room.getWorldObject(new Point(midX+1, midY+1))==null)
			return new Point(midX+1,midY+1);
		return null;
	}

	/**
	 *  GETTERS AND SETTERS (Dragos: I need these to write tests)
	 * @return
	 */
	public Map<Room, Set<Player>> getRoomToPlayers() {
		return roomToPlayers;
	}
	/**
	 *  GETTERS AND SETTERS (Dragos: I need these to write tests)
	 * @return
	 */
	public void setRoomToPlayers(Map<Room, Set<Player>> roomToPlayers) {
		this.roomToPlayers = roomToPlayers;
	}

}
