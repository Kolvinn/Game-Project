package game;


import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The game builder class is used whenever a new game is created. It reads all room files and creates the
 * tile objects within. This class also handles creation of all world objects that occupy the game, the exception to this
 * being the creation of the players.
 * @author Jeremy Purvis
 *
 */
public class GameBuilder {
	public static final Tile  wallTrue = new Tile(true,"wa",0,0); //create a series of placeholder objects
	public static final Tile  emptyTrue = new Tile(true, "fn", 0,0);
	public static final Tile  wallFalse = new Tile(false,"wa",0,0); //create a series of placeholder objects
	public static final Tile  emptyFalse = new Tile(false, "fn", 0,0);

	ArrayList<WorldObject> objects;
	private int arrayIndex = 0;


	/**
	 * constructor called by the gamerunner to build the game
	 * @param roomToPlayers
	 * @param roomNames
	 */
	public GameBuilder(Map<Room, Set<Player>> roomToPlayers, Map<String, Room> roomNames){
		initializeBoard(roomToPlayers, roomNames);
	}

	/**
	 * Creates all rooms and tiles within the game and returns an array list of
	 * said rooms
	 *
	 * @return
	 */
	private void initializeBoard(Map<Room, Set<Player>> roomToPlayers, Map<String, Room> roomNames) {

		//TODO change limit to however many room files there are
		for (int i=0;i<3;i++){
			String roomName ="room"+""+i;
			objects = new ArrayList<WorldObject>();
			Room r = parseRoom(roomName,new File("src/rooms/"+roomName+".txt"), objects);
			roomToPlayers.put(r,new HashSet<Player>());
			roomNames.put(roomName, r);
		}

	}

	/**
	 * Creates a room object from a given file. Each files follows a specification that must be met in order for the
	 * parsing and creation of the room to be done correctly. Each room file also contains in it a specification that determines
	 * what is contained within the room and what other rooms are connected to etc.
	 *
	 * @param file
	 * @return
	 */
	private Room parseRoom(String roomName, File f, ArrayList<WorldObject> objects) {
		ArrayList<String> rowStrings = new ArrayList<String>();
		ArrayList<String> specs = new ArrayList<String>();

		Scanner scan = null;
		try {
			scan = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//scan through the file and place each line in the appropriate list
		while (scan.hasNextLine()) {
			String ss = scan.nextLine();
			if(!ss.startsWith("s"))
				rowStrings.add(ss);
			else
				specs.add(ss.substring(1));
		}

		int x=0,y=0;
		int col = rowStrings.size();
		int row = rowStrings.get(0).split(",").length;

		Tile[][] roomTiles = new Tile[col][row];
		Set<Stairwell> stairs = new HashSet<Stairwell>();


		for(y=0;y<col;y++){
			String[] theRow = rowStrings.get(y).split(",");
			for(x=0;x<row;x++){

				roomTiles[y][x] = createTile(theRow[x],x,y);

				if(roomTiles[y][x] instanceof Stairwell){
					stairs.add((Stairwell )roomTiles[y][x]);
				}
			}
		}


		Room r = new Room(roomName,roomTiles,createConnections(specs, stairs));
		populateRoom(specs, r);
		this.arrayIndex=0;
		return r;

	}

	/**
	 * parse the strings representing staircases and assign them fields to tell them which other rooms they point at
	 * @param specs
	 * @param stairs
	 * @return
	 */
	private Set<Stairwell> createConnections(ArrayList<String> specs, Set<Stairwell> stairs){

		if(specs.get(arrayIndex).equals("Connection")){
			this.arrayIndex++;
			while(specs.get(arrayIndex).startsWith("s")){
				for(String s : specs.get(arrayIndex).split(",")){
					initializeRoomConnection(s, stairs);
				}
				arrayIndex++;
			}
		}
		return stairs;

	}
	/**
	 * parse the Strings in specs into WorldObjects to be placed in room r
	 * @param specs
	 * @param r
	 */
	private void populateRoom(ArrayList<String> specs, Room r){

		if(specs.get(arrayIndex).equals("Specific")){
			if(specs.get(++arrayIndex).equals("true"))
				r.setIsDark(true);
			else
				r.setIsDark(false);
		}

		//create all objects that populate this room
		if(specs.get(++arrayIndex).equals("Objects")){
			this.arrayIndex++;
			WorldObject lastObject =null;
			while(this.arrayIndex<specs.size()){
				String[] objectDesc =specs.get(this.arrayIndex).split(",");
				//if there is no coordinate it means add this object to the lastobject that will be storage
				if(objectDesc[0].equals("null"))
					try {
						((Storage)lastObject).addItem(createWorldObject(objectDesc));
					} catch (GameError e) {
						System.out.println(e.getMessage());
					}
				else{
					lastObject = createWorldObject(objectDesc);
					String[] coos = objectDesc[0].split(":");
					Point p =  new Point(Integer.parseInt(coos[0]),Integer.parseInt(coos[1]));
					r.setWorldObject(p, lastObject);
				}
				arrayIndex++;
			}
		}


	}
	/**
	 * Parses the room connection based on the file format so that code for a given stairwell in a room
	 * will now match a parsed room name
	 * @param pair
	 * @param stairs
	 */
	private void initializeRoomConnection(String pair, Set<Stairwell> stairs){
		String[] connection = pair.split(":");
		for(Stairwell stair : stairs){
			//if the stair name matches the one given in the specification
			if(stair.toString().equals(connection[0])){
				stair.setConnectedRoom(connection[1]);
				stair.setConnectedTile(connection[2]);

				stair.setCrystalCount(Integer.parseInt(connection[3]));
			}
		}
	}
	/**
	 * Creates a world object from the given specification. Each object is layed out in a String in a file such that
	 * parsing the string will return strings that liken to a world object constructor layout
	 * @param objectDescription
	 * @param roomTiles
	 */
	private WorldObject createWorldObject(String[] objectDescription){
		int index=1;
		//determine the type of GameObject
		switch(objectDescription[index]){
		case("Storage"):
			return new Storage(objectDescription[++index], objectDescription[++index], Integer.parseInt( objectDescription[++index]));

		case("Item"):
			return new Item(objectDescription[++index], objectDescription[++index], Boolean.valueOf(objectDescription[++index]));
		case("Sign"):
			return new Sign(objectDescription[++index], objectDescription[++index], objectDescription[++index]);
		case("StairBlock"):
			return new StairBlock(objectDescription[++index], objectDescription[++index], Integer.parseInt( objectDescription[++index]));
		default:
			return new WorldObject(objectDescription[++index], objectDescription[++index]);
		}
	}


	/**
	 * Creates a tile object based on given specification
	 *
	 * @param walkable
	 * @param code
	 * @param x
	 * @param y
	 * @return
	 */
	private Tile createTile(String code, int x, int y) {

		String walkable = code.substring(0,1);
		String tileCode = code.substring(1);
		if (walkable.equals("t")) {
			if (tileCode.startsWith("s"))
				return new Stairwell(true, tileCode, x, y);
			if(tileCode.equals("fn"))
				return emptyTrue;
			return wallFalse;
		} else{
			if(tileCode.equals("fn"))
				return emptyFalse;
			return wallFalse;
		}

	}
}
